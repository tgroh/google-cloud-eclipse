/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.dataflow.eclipse.ui.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * A factory class for creating SWT buttons.
 */
public class ButtonFactory {
  public static Button newPushButton(Composite parent, String label) {
    Button button = new Button(parent, SWT.PUSH);
    button.setText(label);

    PixelConverter converter = new PixelConverter(button);
    int width = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);

    GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false);
    buttonGridData.widthHint =
        Math.max(width, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);

    button.setLayoutData(buttonGridData);
    return button;
  }
}

