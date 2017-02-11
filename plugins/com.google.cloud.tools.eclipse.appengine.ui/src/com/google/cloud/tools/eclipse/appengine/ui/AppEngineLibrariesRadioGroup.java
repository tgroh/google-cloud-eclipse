/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.eclipse.appengine.ui;

import com.google.cloud.tools.eclipse.appengine.libraries.AppEngineLibraries;
import com.google.cloud.tools.eclipse.appengine.libraries.model.Library;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class AppEngineLibrariesRadioGroup  {

  /** The libraries that can be selected. */
  private final Map<String, Library> availableLibraries;

  private final Map<Library, Button> libraryButtons = new LinkedHashMap<>();

  public AppEngineLibrariesRadioGroup(Composite parentContainer) {
    this(parentContainer, AppEngineLibraries.getAvailableLibraries());
  }

  private AppEngineLibrariesRadioGroup(Composite parentContainer,
      Collection<Library> availableLibraries) {
    Preconditions.checkNotNull(parentContainer, "parentContainer is null");
    Preconditions.checkNotNull(availableLibraries, "availableLibraries is null");
    
    this.availableLibraries = new LinkedHashMap<>();
    for (Library library : availableLibraries) {
      this.availableLibraries.put(library.getId(), library);
    }
    createContents(parentContainer);
  }

  private void createContents(Composite parentContainer) {
    Group apiGroup = new Group(parentContainer, SWT.NONE);
    apiGroup.setText(Messages.getString("appengine.library.group"));
    GridDataFactory.swtDefaults().grab(true, false).span(2, 1).applyTo(apiGroup);

    for (Library library : availableLibraries.values()) {
      Button libraryButton = new Button(apiGroup, SWT.RADIO);
      libraryButton.setText(getLibraryName(library));
      if (library.getToolTip() != null) {
        libraryButton.setToolTipText(library.getToolTip());
      }
      libraryButton.setData(library);
      libraryButtons.put(library, libraryButton);
    }
    GridLayoutFactory.fillDefaults().applyTo(apiGroup);
  }

  /**
   * Returns the selected library.
   */
  public Library getSelectedLibrary() {
    for (Entry<Library, Button> entry : libraryButtons.entrySet()) {
      if (entry.getValue().getSelection()) {
        return entry.getKey();
      }
    }
    return null;
  }

  private static String getLibraryName(Library library) {
    if (!Strings.isNullOrEmpty(library.getName())) {
      return library.getName();
    } else {
      return library.getId();
    }
  }

  public void dispose() {
  }
}
