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

import com.google.cloud.tools.eclipse.appengine.ui.AppEngineImages;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.SharedImages;

public class ProjectSelector2 {


  List<String> proposals = Arrays.asList("My Project",
                                         "Hello App Engine",
                                         "This is some other project",
                                         "Yet another hello world",
                                         "Test Project",
                                         "FooBarBaz",
                                         "Cloud Tools For Eclipse testing");
  private Table table;

  /**
   * @param standardDeployPreferencesPanel
   */
  public ProjectSelector2(Composite parent) {
    table = new Table(parent, SWT.MULTI | SWT.BORDER);
    table.setHeaderVisible(true);
    TableColumn icons = new TableColumn(table, SWT.LEFT);
    icons.setText("Project name");
    icons.setWidth(200);
    TableColumn names = new TableColumn(table, SWT.LEFT);
    names.setText("Project ID");
    names.setWidth(200);

    TableItem newProj = new TableItem(table, SWT.NONE);
    newProj.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
    newProj.setText(0, "Create new project");
    for (int i = 0; i < 20; ++i) {
      for (String projectName : proposals) {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        tableItem.setImage(0, AppEngineImages.googleCloudPlatform(16).createImage());
        tableItem.setText(0, projectName);
        tableItem.setText(1, projectName.toLowerCase().replace(' ', '-'));
      }
    }
  }

  public void setLayoutData(Object layoutData) {
    table.setLayoutData(layoutData);
  }
}
