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

import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class ProjectSelector {

  Text text;

  TableCombo tableCombo;
  
  /**
   * 
   */
  public ProjectSelector(Composite parent) {
    tableCombo = new TableCombo(parent, SWT.BORDER | SWT.FLAT);
    tableCombo.defineColumns(1);
    Table table = tableCombo.getTable();
    TableItem item = new TableItem(table, SWT.NONE);
    item.setText("Create new project");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 1");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    item = new TableItem(table, SWT.NONE);
    item.setText("My Project 2");
    tableCombo.setTableWidthPercentage(90);
  }

  /**
   * @param layoutData
   */
  public void setLayoutData(Object layoutData) {
    tableCombo.setLayoutData(layoutData);
  }
}
