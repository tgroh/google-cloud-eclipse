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

package com.google.cloud.tools.eclipse.appengine.deploy.ui;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.tools.eclipse.appengine.deploy.ui.StandardDeployPreferencesPanel.ProjectSelectorSelectionChangedListener;
import com.google.cloud.tools.eclipse.login.ui.AccountSelector;
import com.google.cloud.tools.eclipse.projectselector.ProjectRepository;
import com.google.cloud.tools.eclipse.projectselector.ProjectSelector;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSelectorSelectionChangedListenerTest {

  @Mock private AccountSelector accountSelector;
  @Mock private ProjectSelector projectSelector;
  @Mock private ProjectRepository projectRepository;
  @Mock private SelectionChangedEvent event;

  private ProjectSelectorSelectionChangedListener listener;

  @Before
  public void setUp() throws Exception {
    listener = new ProjectSelectorSelectionChangedListener(accountSelector, projectRepository,
                                                           projectSelector); 
  }

  @Test
  public void testSelectionChanged_emptySelection() {
    when(event.getSelection()).thenReturn(new StructuredSelection());
    listener.selectionChanged(event);
    verify(projectSelector).clearStatusLink();
  }

}
