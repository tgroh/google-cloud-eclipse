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

package com.google.cloud.tools.eclipse.ui.util;

import com.google.cloud.tools.eclipse.util.status.StatusUtil;
import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * {@link SelectionListener} implementation that treats the {@link SelectionEvent#text} as URL
 * and opens it in an external browser.
 */
public class OpenUrlListener implements SelectionListener {

  @Override
  public void widgetSelected(SelectionEvent event) {
    String urlString = event.text;
    try {
      PlatformUI.getWorkbench().getBrowserSupport()
          .getExternalBrowser().openURL(new URL(urlString));
    } catch (PartInitException | MalformedURLException ex) {
      String message = Messages.getString("openurllistener.error.message", urlString);
      ErrorDialog.openError(event.item.getDisplay().getActiveShell(),
                            Messages.getString("openurllistener.error.title"), 
                            message, StatusUtil.error(this, message));
    }
  }

  @Override
  public void widgetDefaultSelected(SelectionEvent event) {
    widgetSelected(event);
  }
}