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

package com.google.cloud.tools.eclipse.appengine.newproject;

import com.google.cloud.tools.eclipse.util.templates.appengine.AppEngineTemplateUtility;
import com.google.common.annotations.VisibleForTesting;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;

public class CodeTemplates {

  /**
   * Load the named template into the supplied Eclipse project.
   *
   * @param project the Eclipse project to be filled with templated code
   * @param config replacement values
   * @param monitor progress monitor
   * @param name directory from which to load template
   */
  public static void materialize(IProject project, AppEngineStandardProjectConfig config,
      IProgressMonitor monitor) throws CoreException {
    SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
    subMonitor.setTaskName("Generating code");
    boolean force = true;
    boolean local = true;
    IFolder src = project.getFolder("src");
    if (!src.exists()) {
      src.create(force, local, subMonitor);
    }
    IContainer main = createChildFolder("main", src, subMonitor);
    IContainer java = createChildFolder("java", main, subMonitor);
    IContainer test = createChildFolder("test", src, subMonitor);
    IContainer testJava = createChildFolder("java", test, subMonitor);

    String packageName = config.getPackageName();

    Map<String, String> templateValues = new HashMap<>();
    if (packageName != null && !packageName.isEmpty()) {
      templateValues.put("package", packageName);
    } else {
      templateValues.put("package", "");
    }

    IContainer packageFolder = createFoldersForPackage(java, packageName, subMonitor);
    createChildFile("HelloAppEngine.java", AppEngineTemplateUtility.HELLO_APPENGINE_TEMPLATE,
        packageFolder, subMonitor, templateValues);

    // now set up the test directory
    IContainer testPackageFolder = createFoldersForPackage(testJava, packageName, subMonitor);
    createChildFile("HelloAppEngineTest.java",
        AppEngineTemplateUtility.HELLO_APPENGINE_TEST_TEMPLATE, testPackageFolder, subMonitor,
        templateValues);
    createChildFile("MockHttpServletResponse.java",
        AppEngineTemplateUtility.MOCK_HTTPSERVLETRESPONSE_TEMPLATE, testPackageFolder, subMonitor,
        templateValues);

    IContainer webapp = createFolders(project, config.getWebContentPath(), subMonitor);
    IContainer webinf = createChildFolder("WEB-INF", webapp, subMonitor);

    Map<String, String> properties = new HashMap<>();
    createChildFile("appengine-web.xml", AppEngineTemplateUtility.APPENGINE_WEB_XML_TEMPLATE,
        webinf, subMonitor, properties);

    Map<String, String> packageMap = new HashMap<>();
    String packageValue = config.getPackageName().isEmpty() ? "" : config.getPackageName() + ".";
    packageMap.put("package", packageValue);
    createChildFile("web.xml", AppEngineTemplateUtility.WEB_XML_TEMPLATE, webinf, subMonitor,
        packageMap);

    createChildFile("index.html", AppEngineTemplateUtility.INDEX_HTML_TEMPLATE, webapp, subMonitor,
        Collections.<String, String>emptyMap());
  }

  private static IContainer createFoldersForPackage(IContainer parentFolder,
                                                    String packageName,
                                                    SubMonitor subMonitor) throws CoreException {
    String path = packageName.replace('.', File.separatorChar);
    return createFolders(parentFolder, path, subMonitor);
  }

  private static IContainer createFolders(IContainer parentFolder,
                                          String path,
                                          SubMonitor subMonitor) throws CoreException {
    IContainer folder = parentFolder;
    if (path != null && !path.isEmpty()) {
      String[] directories = path.split(File.separator);
      for (String directory : directories) {
        folder = createChildFolder(directory, folder, subMonitor);
      }
    }
    return folder;
  }

  @VisibleForTesting
  static IContainer createChildFolder(String name, IContainer parent, SubMonitor monitor)
      throws CoreException {
    monitor.subTask("Creating folder " + name);
    monitor.newChild(10);

    boolean force = true;
    boolean local = true;
    IFolder child = parent.getFolder(new Path(name));
    if (!child.exists()) {
      child.create(force, local, monitor);
    }
    return child;
  }

  @VisibleForTesting
  static IFile createChildFile(String name, String template, IContainer parent, SubMonitor monitor,
      Map<String, String> values) throws CoreException {

    monitor.subTask("Creating file " + name);
    monitor.newChild(20);

    IFile child = parent.getFile(new Path(name));
    if (!child.exists()) {
      child.create(new ByteArrayInputStream(new byte[0]), true /* force */, monitor);
      AppEngineTemplateUtility.createFileContent(
          child.getLocation().toString(), template, values);
      child.refreshLocal(IResource.DEPTH_ZERO, monitor);
    }
    return child;
  }

}
