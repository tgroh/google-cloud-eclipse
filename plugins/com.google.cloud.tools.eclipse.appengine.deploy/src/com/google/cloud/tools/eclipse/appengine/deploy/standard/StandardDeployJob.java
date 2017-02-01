/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.eclipse.appengine.deploy.standard;

import com.google.api.client.auth.oauth2.Credential;
import com.google.cloud.tools.appengine.api.deploy.DefaultDeployConfiguration;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessExitListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessOutputLineListener;
import com.google.cloud.tools.appengine.cloudsdk.process.ProcessStartListener;
import com.google.cloud.tools.eclipse.appengine.deploy.AppEngineDeployUtil;
import com.google.cloud.tools.eclipse.appengine.deploy.AppEngineDeployUtil.DeployOutput;
import com.google.cloud.tools.eclipse.appengine.deploy.AppEngineProjectDeployer;
import com.google.cloud.tools.eclipse.appengine.deploy.Messages;
import com.google.cloud.tools.eclipse.login.CredentialHelper;
import com.google.cloud.tools.eclipse.sdk.CollectingLineListener;
import com.google.cloud.tools.eclipse.sdk.ui.MessageConsoleWriterOutputLineListener;
import com.google.cloud.tools.eclipse.util.CloudToolsInfo;
import com.google.cloud.tools.eclipse.util.status.StatusUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.progress.UIJob;

/**
 * Executes a job that deploys a project to App Engine Standard.
 * <p>
 * Deploy steps:
 * <ol>
 *  <li>export exploded WAR</li>
 *  <li>stage project for deploy</li>
 *  <li>deploy staged project</li>
 *  <li>launch the deployed app in browser</li>
 * </ol>
 * It uses a work directory where it will create separate directories for the exploded WAR and the
 * staging results.
 */
// TODO: add tests
public class StandardDeployJob extends WorkspaceJob {

  private static final String STAGING_DIRECTORY_NAME = "staging";
  private static final String EXPLODED_WAR_DIRECTORY_NAME = "exploded-war";
  private static final String CREDENTIAL_FILENAME = "gcloud-credentials.json";
  private static final String ERROR_MESSAGE_PREFIX = "ERROR:";

  private static final Logger logger = Logger.getLogger(StandardDeployJob.class.getName());

  //temporary way of error handling, after #439 is fixed, it'll be cleaner
  private IStatus cloudSdkProcessStatus = Status.OK_STATUS;
  private Process process;

  private IProject project;
  private Credential credential;
  protected IPath workDirectoryParent;
  private MessageConsoleWriterOutputLineListener stdoutLineListener;
  private ProcessOutputLineListener stderrLineListener;
  private DefaultDeployConfiguration deployConfiguration;
  private CollectingLineListener errorCollectingLineListener;
  private int stagingOutputEndIndex = 0;

  public StandardDeployJob(IProject project,
                           Credential credential,
                           IPath workDirectoryParent,
                           MessageConsoleWriterOutputLineListener stdoutLineListener,
                           ProcessOutputLineListener stderrLineListener,
                           DefaultDeployConfiguration deployConfiguration) {
    super(Messages.getString("deploy.standard.runnable.name")); //$NON-NLS-1$
    this.project = project;
    this.credential = credential;
    this.workDirectoryParent = workDirectoryParent;
    this.stdoutLineListener = stdoutLineListener;
    this.stderrLineListener = stderrLineListener;
    this.deployConfiguration = deployConfiguration;
    errorCollectingLineListener =
        new CollectingLineListener(new Predicate<String>() {
                                     @Override
                                     public boolean apply(String line) {
                                       return line != null
                                           && line.startsWith(ERROR_MESSAGE_PREFIX);
                                     }
                                   });
  }

  @Override
  public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
    SubMonitor progress = SubMonitor.convert(monitor, 100);
    stagingOutputEndIndex = 0;
    IStatus status;
    Path credentialFile = null;

    try {
      IPath workDirectory = workDirectoryParent;
      IPath explodedWarDirectory = workDirectory.append(EXPLODED_WAR_DIRECTORY_NAME);
      IPath stagingDirectory = workDirectory.append(STAGING_DIRECTORY_NAME);
      credentialFile = workDirectory.append(CREDENTIAL_FILENAME).toFile().toPath();

      status = saveCredential(credentialFile, credential);
      if (status != Status.OK_STATUS) {
        return status;
      }

      status =
          stageProject(credentialFile, explodedWarDirectory, stagingDirectory, progress.newChild(30));
      if (status != Status.OK_STATUS) {
        return status;
      }

      status = deployProject(credentialFile, stagingDirectory, progress.newChild(70));
      if (status != Status.OK_STATUS) {
        return status;
      }

      return openAppInBrowser();
    } finally {
      if (credentialFile != null) {
        try {
          Files.delete(credentialFile);
        } catch (IOException exception) {
          logger.log(Level.WARNING, "Could not delete credential file after deploy", exception);
        }
      }
      monitor.done();
    }
  }

  @Override
  protected void canceling() {
    cloudSdkProcessStatus = Status.CANCEL_STATUS;
    if (process != null) {
      process.destroy();
    }
    super.canceling();
  }

  private IStatus saveCredential(Path destination, Credential credential) {
    String jsonCredential = new CredentialHelper().toJson(credential);
    try {
      Files.write(destination, jsonCredential.getBytes(StandardCharsets.UTF_8));
    } catch (IOException ex) {
      return StatusUtil.error(getClass(), Messages.getString("save.credential.failed"), ex);
    }
    return Status.OK_STATUS;
  }

  private IStatus stageProject(Path credentialFile, IPath explodedWarDirectory, IPath stagingDirectory, IProgressMonitor monitor) {
    SubMonitor progress = SubMonitor.convert(monitor, 100);
    StagingExitListener stagingExitListener = new StagingExitListener();
    CloudSdk cloudSdk = getCloudSdk(credentialFile, stagingExitListener);

    try {
      getJobManager().beginRule(project, progress);
      new ExplodedWarPublisher().publish(project, explodedWarDirectory, progress.newChild(40));
      new StandardProjectStaging().stage(explodedWarDirectory, stagingDirectory, cloudSdk, progress.newChild(60));
      return stagingExitListener.getExitStatus();
    } catch (CoreException | IllegalArgumentException | OperationCanceledException ex) {
      return StatusUtil.error(getClass(), "Error staging project " + project.getName() + "; " + ex.getMessage());
    } finally {
      getJobManager().endRule(project);
    }
  }

  private IStatus deployProject(Path credentialFile, IPath stagingDirectory, IProgressMonitor monitor) {
    DeployExitListener deployExitListener = new DeployExitListener();
    CloudSdk cloudSdk = getCloudSdk(credentialFile, deployExitListener);
    new AppEngineProjectDeployer().deploy(
        stagingDirectory, cloudSdk, deployConfiguration, monitor);
    return deployExitListener.getExitStatus();
  }

  private CloudSdk getCloudSdk(Path credentialFile, ProcessExitListener processExitListener) {
    CloudSdk cloudSdk = new CloudSdk.Builder()
                          .addStdOutLineListener(stdoutLineListener)
                          .addStdErrLineListener(stderrLineListener)
                          .addStdErrLineListener(errorCollectingLineListener)
                          .appCommandCredentialFile(credentialFile.toFile())
                          .startListener(new StoreProcessObjectListener())
                          .exitListener(processExitListener)
                          .appCommandMetricsEnvironment(CloudToolsInfo.METRICS_NAME)
                          .appCommandMetricsEnvironmentVersion(CloudToolsInfo.getToolsVersion())
                          .appCommandOutputFormat("json")
                          .build();
    return cloudSdk;
  }

  private IStatus openAppInBrowser() {
    final String project = deployConfiguration.getProject();
    String appLocation = null;
    if (deployConfiguration.getPromote()) {
      appLocation = "http://" + project + ".appspot.com";
    } else {
      try {
        String version =  getDeployedAppVersion();
        appLocation = "http://" + version + "-dot-" + project+ ".appspot.com/";
      } catch (IndexOutOfBoundsException | JsonParseException ex)  {
        return StatusUtil.error(getClass(), "Error launching deployed app in browser", ex);
      }
    }

    final String finalAppLocation = appLocation;
    final IWorkbench workbench = PlatformUI.getWorkbench();
    Job launchBrowserJob = new UIJob(workbench.getDisplay(), "Launch deployed app in browser") {

      @Override
      public IStatus runInUIThread(IProgressMonitor monitor) {
        try {
          URL url = new URL(finalAppLocation);
          IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();
          int style = IWorkbenchBrowserSupport.LOCATION_BAR
              | IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS;
          browserSupport.createBrowser(style, project, project, project).openURL(url);
        } catch (PartInitException ex) {
          // Unable to use the normal browser support, so push to the OS
          logger.log(Level.WARNING, "Cannot launch a browser", ex);
          Program.launch(finalAppLocation);
        } catch (MalformedURLException ex) {
          return StatusUtil.error(getClass(), "Invalid URL", ex);
        }
        return Status.OK_STATUS;
      }

    };
    launchBrowserJob.schedule();
    return Status.OK_STATUS;
  }

  /**
   * @return the error message obtained from <code>config.getErrorMessageProvider()</code> or
   * <code>defaultMessage</code>
   */
  private String getErrorMessageOrDefault(String defaultMessage) {
    // TODO: Check the assumption that if there are error messages during staging collected via
    // the errorCollectingLineListener, the staging process will have a non-zero exitcode,
    // so it is ok to use the same errorCollectingLineListener for the deploy process
    List<String> messages = errorCollectingLineListener.getCollectedMessages();
    if (!messages.isEmpty()) {
      return Joiner.on('\n').join(messages);
    } else {
      return defaultMessage;
    }
  }

  private String getDeployedAppVersion() {
    String rawDeployOutput = stdoutLineListener.getOutput().substring(stagingOutputEndIndex);
    DeployOutput deployOutput = AppEngineDeployUtil.parseDeployOutput(rawDeployOutput);
    if (deployOutput == null || deployOutput.getVersion() == null) {
      throw new JsonParseException("Cannot get app version: unexpected gcloud JSON output format");
    }

    return deployOutput.getVersion();
  }

  private final class StoreProcessObjectListener implements ProcessStartListener {
    @Override
    public void onStart(Process proces) {
      process = proces;
    }
  }

  private class StagingExitListener implements ProcessExitListener {
    private IStatus status;

    @Override
    public void onExit(int exitCode) {
      if (cloudSdkProcessStatus == Status.CANCEL_STATUS) {
        status = cloudSdkProcessStatus;
      } else if (exitCode != 0) {
        // temporary way of error handling, after #439 is fixed, it'll be cleaner
        String errorMessage =
            getErrorMessageOrDefault(Messages.getString("cloudsdk.process.failed", exitCode));
        status = StatusUtil.error(this, errorMessage);
      } else {
        stagingOutputEndIndex = stdoutLineListener.getOutput().length();
        status = Status.OK_STATUS;
      }
    }

    /**
     * @return the status on exit of the process or null is the process has not exited.
     */
    public IStatus getExitStatus() {
      return status;
    }
  }

  private final class DeployExitListener implements ProcessExitListener {
    private IStatus status;

    @Override
    public void onExit(int exitCode) {
      if (cloudSdkProcessStatus == Status.CANCEL_STATUS) {
        status = cloudSdkProcessStatus;
      } else if (exitCode != 0) {
        // temporary way of error handling, after #439 is fixed, it'll be cleaner
        String errorMessage =
            getErrorMessageOrDefault(Messages.getString("cloudsdk.process.failed", exitCode));
        status = StatusUtil.error(this, errorMessage);
      } else {
        status = Status.OK_STATUS;
      }
    }

    /**
     * @return the status on exit of the process or null is the process has not exited.
     */
    public IStatus getExitStatus() {
      return status;
    }
  }

}