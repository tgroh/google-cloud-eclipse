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

package com.google.cloud.tools.eclipse.appengine.libraries.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension2;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.google.cloud.tools.eclipse.appengine.libraries.BuildPath;
import com.google.cloud.tools.eclipse.appengine.libraries.model.Library;
import com.google.cloud.tools.eclipse.appengine.ui.AppEngineImages;
import com.google.cloud.tools.eclipse.appengine.ui.AppEngineLibrariesRadioGroup;

// todo use IClasspathContainerPageExtension2 to return more than one container
public class AppEngineLibrariesPage extends WizardPage 
    implements IClasspathContainerPage, IClasspathContainerPageExtension, IClasspathContainerPageExtension2 {

  private AppEngineLibrariesRadioGroup librariesSelector;
  private IJavaProject project;

  public AppEngineLibrariesPage() {
    super("appengine-libraries-page"); //$NON-NLS-1$
    setTitle(Messages.getString("title"));
    setDescription(Messages.getString("description"));
    setImageDescriptor(AppEngineImages.appEngine(64));
  }

  @Override
  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.BORDER);
    composite.setLayout(new GridLayout(2, true));
    
    librariesSelector = new AppEngineLibrariesRadioGroup(composite);
    
    setControl(composite);
  }

  @Override
  public boolean finish() {
    return true;
  }

  @Override
  public IClasspathEntry getSelection() {
    // this should not be called by eclipse
    return null;
  }

  @Override
  public void setSelection(IClasspathEntry containerEntry) {
  }

  @Override
  public void initialize(IJavaProject project, IClasspathEntry[] currentEntries) {
    System.err.println("Initializing...");
    for (IClasspathEntry entry: currentEntries) {
      System.err.println(entry.getPath());
    }
    // TODO fill in existing libraries. Initialize is called before createControl.
    // maybe don't include libraries already in build path.
    // maybe add note of what's already included in the page
    
    this.project = project;
  }

  @Override
  public IClasspathEntry[] getNewContainers() {
    Library library = librariesSelector.getSelectedLibrary();
// todo handle mutliple entries
    ArrayList<IClasspathEntry> added = new ArrayList<>();
    if (library == null) {
      return null;
    }
    try {
      ArrayList<Library> libraries = new ArrayList<Library>();
      libraries.add(library);
      IClasspathEntry entry = BuildPath.addLibraries(project, libraries, new NullProgressMonitor());
      added.add(entry);
    } catch (CoreException ex) {
      // todo handle build path contains duplicate entry; don't add duplicate entries
      System.err.println(ex.getMessage());
    }
    return added.toArray(new IClasspathEntry[0]);
  }

}