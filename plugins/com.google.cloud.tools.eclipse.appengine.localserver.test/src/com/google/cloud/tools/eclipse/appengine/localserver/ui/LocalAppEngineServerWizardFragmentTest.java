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

import com.google.cloud.tools.eclipse.appengine.localserver.Messages;
import org.eclipse.osgi.util.NLS;
import org.junit.Assert;
import org.junit.Test;

public class LocalAppEngineServerWizardFragmentTest {
  private LocalAppEngineServerWizardFragment wizardFragment;

  @Test
  public void testHasComposite() {
    wizardFragment = new LocalAppEngineServerWizardFragment();
    Assert.assertTrue(wizardFragment.hasComposite());
  }

  @Test
  public void testIsComplete_cloudSdkExists() {
    wizardFragment = new LocalAppEngineServerWizardFragment("cloudSdkLocation");
    Assert.assertTrue(wizardFragment.isComplete());
  }

  @Test
  public void testIsComplete_cloudSdkDoesNotExists() {
    wizardFragment = new LocalAppEngineServerWizardFragment(null);
    Assert.assertFalse(wizardFragment.isComplete());
  }

  @Test
  public void testCloudSdkConfigurationMessage_cloudSdkExists() {
    wizardFragment = new LocalAppEngineServerWizardFragment("cloudSdkLocation");
    Assert.assertEquals(NLS.bind(Messages.RUNTIME_WIZARD_CLOUD_SDK_FOUND, "cloudSdkLocation"), 
        wizardFragment.getCloudSdkConfigurationMessage());
  }

  @Test
  public void testCloudSdkConfigurationMessage_cloudSdkDoesNotExists() {
    wizardFragment = new LocalAppEngineServerWizardFragment(null);
    Assert.assertEquals(Messages.RUNTIME_WIZARD_CLOUD_SDK_NOT_FOUND,
        wizardFragment.getCloudSdkConfigurationMessage());
  }
}
