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

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ProjectSelector2 {

  private Combo combo;
  private ComboViewer comboViewer;

  List<String> proposals = Arrays.asList("My Project",
                                         "Hello App Engine",
                                         "This is some other project");
  
  private String filterString = "";
  private boolean handleEvent = false;
  /**
   * @param standardDeployPreferencesPanel
   */
  public ProjectSelector2(Composite parent) {
    comboViewer = new ComboViewer(parent, SWT.DROP_DOWN);
    combo = comboViewer.getCombo();
    
    comboViewer.setContentProvider(new IStructuredContentProvider() {
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
      
      @Override
      public void dispose() {
      }
      
      @Override
      public Object[] getElements(Object inputElement) {
        return proposals.toArray(new String[0]);
      }
    });
    comboViewer.setInput(combo); // could be something else, doesn't matter for now
    comboViewer.addFilter(new ViewerFilter() {
      
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return Strings.isNullOrEmpty(filterString) || ((String) element).contains(filterString);
      }
    });
    combo.addModifyListener(new ModifyListener() {
      
      @Override
      public void modifyText(ModifyEvent e) {
        System.out.println("Combo text changed to: " + combo.getText());
        handleEvent = !handleEvent;
        if (handleEvent) {
          filterString = combo.getText();
          comboViewer.refresh();
          handleEvent = !handleEvent; // flip again, so that the setText on the following line won't be handled in this if block
          combo.setText(filterString);
        }
      }
    });
  }
  
  public void setLayoutData(Object layoutData) {
    combo.setLayoutData(layoutData);
  }
}
