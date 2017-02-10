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

package com.google.cloud.tools.eclipse.appengine.deploy;

import com.google.cloud.tools.appengine.api.deploy.DefaultDeployConfiguration;
import com.google.cloud.tools.eclipse.appengine.deploy.standard.StandardDeployJob;
import org.junit.Assert;
import org.junit.Test;

public class StandardDeployJobTest {
  @Test
  public void testGetDeployedAppUrl_promoteWithDefaultService() {    
    DefaultDeployConfiguration config = new DefaultDeployConfiguration();
    config.setPromote(true);
    config.setProject("testProject");
    StandardDeployJob standardDeployJob = new StandardDeployJob(null, null, null, null, null, config);
    AppEngineDeployOutput deployOutput = new AppEngineDeployOutput("version", "default");

    Assert.assertEquals("https://testProject.appspot.com",
        standardDeployJob.getDeployedAppUrl(deployOutput));
  }
  
  @Test
  public void testGetDeployedAppUrl_promoteWithNonDefaultService() {
    DefaultDeployConfiguration config = new DefaultDeployConfiguration();
    config.setPromote(true);
    config.setProject("testProject");
    StandardDeployJob standardDeployJob = new StandardDeployJob(null, null, null, null, null, config);
    AppEngineDeployOutput deployOutput = new AppEngineDeployOutput("version", "service");

    Assert.assertEquals("https://service-dot-testProject.appspot.com",
        standardDeployJob.getDeployedAppUrl(deployOutput));
  }
  
  @Test
  public void testGetDeployedAppUrl_noPromoteWithDefaultService() {
    DefaultDeployConfiguration config = new DefaultDeployConfiguration();
    config.setPromote(false);
    config.setProject("testProject");
    StandardDeployJob standardDeployJob = new StandardDeployJob(null, null, null, null, null, config);
    AppEngineDeployOutput deployOutput = new AppEngineDeployOutput("version", "default");

    Assert.assertEquals("https://version-dot-testProject.appspot.com",
        standardDeployJob.getDeployedAppUrl(deployOutput));
  }
  
  @Test
  public void testGetDeployedAppUrl_noPromoteWithNonDefaultService() {
    DefaultDeployConfiguration config = new DefaultDeployConfiguration();
    config.setPromote(false);
    config.setProject("testProject");
    StandardDeployJob standardDeployJob = new StandardDeployJob(null, null, null, null, null, config);
    AppEngineDeployOutput deployOutput = new AppEngineDeployOutput("version", "service");

    Assert.assertEquals("https://version-dot-service-dot-testProject.appspot.com",
        standardDeployJob.getDeployedAppUrl(deployOutput));
  }
}
