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

package com.google.cloud.tools.eclipse.appengine.deploy.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.client.auth.oauth2.Credential;
import com.google.cloud.tools.eclipse.appengine.deploy.standard.StandardDeployPreferences;
import com.google.cloud.tools.eclipse.login.IGoogleLoginService;
import com.google.cloud.tools.eclipse.login.ui.AccountSelectorObservableValue;
import com.google.cloud.tools.eclipse.projectselector.GcpProject;
import com.google.cloud.tools.eclipse.projectselector.ProjectRepository;
import com.google.cloud.tools.eclipse.projectselector.ProjectRepositoryException;
import com.google.cloud.tools.eclipse.projectselector.ProjectSelector;
import com.google.cloud.tools.eclipse.test.util.ui.ShellTestResource;
import com.google.cloud.tools.login.Account;
import java.util.Arrays;
import java.util.HashSet;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardDeployPreferencesPanelTest {

  private static final String EMAIL_2 = "some-email-2@example.com";
  private static final String EMAIL_1 = "some-email-1@example.com";

  private Composite parent;
  @Mock private IProject project;
  @Mock private IGoogleLoginService loginService;
  @Mock private Runnable layoutChangedHandler;
  @Mock private Account account1;
  @Mock private Account account2;
  @Mock private Credential credential;
  @Mock private ProjectRepository projectRepository;
  @Rule public ShellTestResource shellTestResource = new ShellTestResource();

  @Before
  public void setUp() throws Exception {
    parent = new Composite(shellTestResource.getShell(), SWT.NONE);
    when(project.getName()).thenReturn("testProject");
    when(account1.getEmail()).thenReturn(EMAIL_1);
    when(account2.getEmail()).thenReturn(EMAIL_2);
    when(account1.getOAuth2Credential()).thenReturn(credential);
    when(account2.getOAuth2Credential()).thenReturn(mock(Credential.class));
  }

  @Test
  public void testSelectSingleAccount() {
    when(loginService.getAccounts()).thenReturn(new HashSet<>(Arrays.asList(account1)));
    StandardDeployPreferencesPanel deployPanel = new StandardDeployPreferencesPanel(
        parent, project, loginService, layoutChangedHandler, true, projectRepository);
    assertThat(deployPanel.getSelectedCredential(), is(credential));

    // todo? assertTrue(deployPanel.getAccountSelector().isAutoSelectAccountIfNone()

    // verify not in error
    IStatus status = getAccountSelectorValidationStatus(deployPanel);
    assertTrue("account selector is in error: " + status.getMessage(), status.isOK());

    assertThat("auto-selected value should be propagated back to model",
        deployPanel.model.getAccountEmail(), is(account1.getEmail()));
  }

  @Test
  public void testValidationMessageWhenNotSignedIn() {
    StandardDeployPreferencesPanel deployPanel =
        new StandardDeployPreferencesPanel(parent, project, loginService, layoutChangedHandler,
                                           true, projectRepository);
    IStatus status = getAccountSelectorValidationStatus(deployPanel);
    assertThat(status.getMessage(), is("Sign in to Google."));
  }

  @Test
  public void testValidationMessageWhenSignedIn() {
    // Return two accounts because the account selector will auto-select if there exists only one.
    when(loginService.getAccounts()).thenReturn(new HashSet<>(Arrays.asList(account1, account2)));

    StandardDeployPreferencesPanel deployPanel =
        new StandardDeployPreferencesPanel(parent, project, loginService, layoutChangedHandler,
                                           true, projectRepository);
    IStatus status = getAccountSelectorValidationStatus(deployPanel);
    assertThat(status.getMessage(), is("Select an account."));
  }

  @Test
  public void testProjectSavedInPreferencesSelected() throws ProjectRepositoryException {
    IEclipsePreferences node =
        new ProjectScope(project).getNode(StandardDeployPreferences.PREFERENCE_STORE_QUALIFIER);
    node.put("project.id", "projectId1");
    node.put("account.email", EMAIL_1);
    initializeProjectRepository(projectRepository);
    when(loginService.getAccounts()).thenReturn(new HashSet<>(Arrays.asList(account1, account2)));
    StandardDeployPreferencesPanel deployPanel =
        new StandardDeployPreferencesPanel(parent, project, loginService, layoutChangedHandler,
                                           true, projectRepository);
    for (Control control : deployPanel.getChildren()) {
      if (control instanceof ProjectSelector) {
        ProjectSelector projectSelector = (ProjectSelector) control;
        IStructuredSelection selection = projectSelector.getViewer().getStructuredSelection();
        assertThat(selection.size(), is(1));
        assertThat(((GcpProject) selection.getFirstElement()).getId(), is("projectId1"));
        return;
      }
    };
    fail("Did not find ProjectSelector widget");
  }

  private void initializeProjectRepository(ProjectRepository projectRepository)
      throws ProjectRepositoryException {
    GcpProject project1 = new GcpProject("Project1", "projectId1");
    GcpProject project2 = new GcpProject("Project2", "projectId2");
    when(projectRepository.getProjects(any(Credential.class)))
      .thenReturn(Arrays.asList(project1, project2));
    when(projectRepository.getProject(any(Credential.class), eq("projectId1")))
        .thenReturn(project1);
    when(projectRepository.getProject(any(Credential.class), eq("projectId2")))
        .thenReturn(project2);
  }

  private IStatus getAccountSelectorValidationStatus(StandardDeployPreferencesPanel deployPanel) {
    IStatus status = null;
    for (Object object : deployPanel.getDataBindingContext().getValidationStatusProviders()) {
      ValidationStatusProvider statusProvider = (ValidationStatusProvider) object;
      if (!statusProvider.getTargets().isEmpty()) {
        if (statusProvider.getTargets().get(0) instanceof AccountSelectorObservableValue) {
          status = (IStatus) statusProvider.getValidationStatus().getValue();
          if (!status.isOK()) {
            return status;
          }
        }
      }
    }
    if (status == null) {
      fail("Could not find AccountSelector databinding to verify validation");
    }
    return status;
  }

}
