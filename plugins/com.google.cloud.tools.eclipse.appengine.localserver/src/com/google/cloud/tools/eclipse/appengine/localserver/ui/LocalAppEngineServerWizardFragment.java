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

package com.google.cloud.tools.eclipse.appengine.localserver.ui;

import com.google.cloud.tools.appengine.api.AppEngineException;
import com.google.cloud.tools.appengine.cloudsdk.CloudSdk;
import com.google.cloud.tools.eclipse.appengine.localserver.Messages;
import com.google.cloud.tools.eclipse.sdk.ui.preferences.CloudSdkPreferenceArea;
import com.google.common.annotations.VisibleForTesting;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

public class LocalAppEngineServerWizardFragment extends WizardFragment {
  private final String cloudSdkLocation;

  public LocalAppEngineServerWizardFragment() {
    cloudSdkLocation = getCloudSdkLocation();
  }

  @VisibleForTesting
  LocalAppEngineServerWizardFragment(String cloudSdkLocation)
  {
    this.cloudSdkLocation = cloudSdkLocation;
  }

  @Override
  public boolean hasComposite() {
    return (cloudSdkLocation == null);
  }

  @Override
  public boolean isComplete() {
    return (cloudSdkLocation != null);
  }

  @Override
  public Composite createComposite(final Composite parent, IWizardHandle wizard) {
    wizard.setTitle(Messages.CREATE_APP_ENGINE_RUNTIME_WIZARD_TITLE);
    wizard.setDescription(Messages.CREATE_APP_ENGINE_RUNTIME_WIZARD_DESCRIPTION);
    
    Composite cloudSdkComposite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    cloudSdkComposite.setLayout(layout);

    Label label = new Label(cloudSdkComposite, SWT.NONE);
    label.setText(Messages.RUNTIME_WIZARD_CLOUD_SDK_NOT_FOUND);

    Button button = new Button(cloudSdkComposite, SWT.PUSH);
    button.setText("Update SDK Location");
    button.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent event) {
        Shell shell = parent.getShell();
        if (!MessageDialog.openQuestion(shell, Messages.RUNTIME_WIZARD_OPEN_CLOUD_SDK_DIALOG_TITLE, 
            Messages.RUNTIME_WIZARD_OPEN_CLOUD_SDK_DIALOG_MESSAGE)) {
          return;
        }
        shell.close();

        PreferenceDialog dialog =
            PreferencesUtil.createPreferenceDialogOn(null, CloudSdkPreferenceArea.PAGE_ID, null, null);
        dialog.open();
      }
    });
    
    return cloudSdkComposite;
  }

  private String getCloudSdkLocation() {
    try {
      CloudSdk cloudSdk = new CloudSdk.Builder().build();
      return cloudSdk.getSdkPath().toString();
    } catch (AppEngineException ex) {
      return null;
    }
  }
}
