/*
 * Copyright 2017 Google Inc.
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

import com.google.cloud.tools.eclipse.appengine.libraries.model.Library;
import com.google.cloud.tools.eclipse.appengine.ui.AppEngineImages;
import com.google.cloud.tools.eclipse.appengine.ui.AppEngineLibrariesSelectorGroup;
import com.google.cloud.tools.eclipse.usagetracker.AnalyticsEvents;
import com.google.cloud.tools.eclipse.usagetracker.AnalyticsPingManager;
import java.io.File;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * UI to collect all information necessary to create a new App Engine Java Eclipse project.
 */
public class AppEngineWizardPage extends WizardNewProjectCreationPage {

  private Text javaPackageField;
  private AppEngineLibrariesSelectorGroup appEngineLibrariesSelectorGroup;
  private Text serviceNameField;

  public AppEngineWizardPage() {
    super("basicNewProjectPage"); //$NON-NLS-1$
    setTitle(Messages.getString("app.engine.project")); //$NON-NLS-1$
    setDescription(Messages.getString("create.app.engine.project")); //$NON-NLS-1$
    setImageDescriptor(AppEngineImages.appEngine(64));
  }

  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);
    AnalyticsPingManager.getInstance().sendPing(
        AnalyticsEvents.APP_ENGINE_NEW_PROJECT_WIZARD,
        AnalyticsEvents.APP_ENGINE_NEW_PROJECT_WIZARD_TYPE,
        AnalyticsEvents.APP_ENGINE_NEW_PROJECT_WIZARD_TYPE_NATIVE, parent.getShell());

    Composite container = (Composite) getControl();
    PlatformUI.getWorkbench().getHelpSystem().setHelp(container,
        "com.google.cloud.tools.eclipse.appengine.newproject.NewProjectContext"); //$NON-NLS-1$

    ModifyListener pageValidator = new PageValidator();
    createCustomFields(container, pageValidator);

    // Manage APIs
    appEngineLibrariesSelectorGroup = new AppEngineLibrariesSelectorGroup(container);

    setPageComplete(validatePage());
    // Show enter project name on opening
    setErrorMessage(null);
    setMessage(Messages.getString("enter.project.name"));

    Dialog.applyDialogFont(container);
  }

  private void createCustomFields(Composite container, ModifyListener pageValidator) {
    Composite composite = new Composite(container, SWT.NONE);
    GridDataFactory.fillDefaults().applyTo(composite);

    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout(layout);

    createMavenCheckbox(composite);
    createEnvironmentRadioButtons(composite);
    createPackageField(composite, pageValidator);
    createServiceField(composite, pageValidator);
  }

  private void createMavenCheckbox(Composite composite) {
    Button checkbox = new Button(composite, SWT.CHECK);
    checkbox.setText("Use Maven");
    
    GridData gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.horizontalSpan = 2;
    checkbox.setLayoutData(gridData);
  }

  private void createEnvironmentRadioButtons(Composite composite) {
    Group buttonGroup = new Group(composite, SWT.NONE);
    GridLayout gridLayout = new GridLayout();
    buttonGroup.setLayout(gridLayout);
    GridDataFactory.fillDefaults().span(2, 1).applyTo(buttonGroup);

    
    
    Button standard = new Button(buttonGroup, SWT.RADIO);
    standard.setSelection(true);
    Button flex = new Button(buttonGroup, SWT.RADIO);
    standard.setText("App Engine Standard");
    flex.setText("App Engine Flex");
    
    /*GridData gridData = new GridData(GridData.FILL_VERTICAL);
    gridData.horizontalSpan = 2;
    buttonGroup.setLayoutData(gridData);*/
    
    GridLayoutFactory.fillDefaults().applyTo(buttonGroup);

  }

  // Java package name
  private void createPackageField(Composite parent, ModifyListener pageValidator) {
    Label packageNameLabel = new Label(parent, SWT.LEAD);
    packageNameLabel.setText(Messages.getString("java.package")); //$NON-NLS-1$
    javaPackageField = new Text(parent, SWT.BORDER);

    javaPackageField.addModifyListener(pageValidator);

    GridDataFactory.fillDefaults().grab(true, false).applyTo(javaPackageField);
  }

  // App Engine service name
  private void createServiceField(Composite parent, ModifyListener pageValidator) {

    Label serviceNameLabel = new Label(parent, SWT.LEAD);
    serviceNameLabel.setText(Messages.getString("app.engine.service"));
    serviceNameField = new Text(parent, SWT.BORDER);
    serviceNameField.setMessage("default");  //$NON-NLS-1$
    serviceNameField.addModifyListener(pageValidator);

    GridDataFactory.fillDefaults().grab(true, false).applyTo(serviceNameField);
  }

  @Override
  public boolean validatePage() {
    setErrorMessage(null);
    setMessage(null);

    if (!super.validatePage()) {
      return false;
    }

    return validateLocalFields();
  }

  private boolean validateLocalFields() {
    String packageName = javaPackageField.getText();
    IStatus packageStatus = JavaPackageValidator.validate(packageName);
    if (!packageStatus.isOK()) {
      String message = Messages.getString("illegal.package.name",  //$NON-NLS-1$
          packageStatus.getMessage());
      setErrorMessage(message);
      return false;
    }

    File parent = getLocationPath().toFile();
    File projectDirectory = new File(parent, getProjectName());
    if (projectDirectory.exists()) {
      setErrorMessage(
          Messages.getString("project.location.exists", projectDirectory)); //$NON-NLS-1$
      return false;
    }

    return true;
  }

  private final class PageValidator implements ModifyListener {
    @Override
    public void modifyText(ModifyEvent event) {
      setPageComplete(validatePage());
    }
  }

  public String getPackageName() {
    return javaPackageField.getText();
  }

  public Collection<Library> getSelectedLibraries() {
    return appEngineLibrariesSelectorGroup.getSelectedLibraries();
  }

  @Override
  public void dispose() {
    appEngineLibrariesSelectorGroup.dispose();
    super.dispose();
  }

  public String getServiceName() {
    return serviceNameField.getText();
  }
}
