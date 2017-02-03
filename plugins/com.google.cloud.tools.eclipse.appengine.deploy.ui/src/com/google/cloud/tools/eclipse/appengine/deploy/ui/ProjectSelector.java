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

import com.google.common.base.Predicates;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ProjectSelector {

  private Text text;

  List<String> proposals = Arrays.asList("My Project", "Hello App Engine", "This is some other project");
  /**
   * @param standardDeployPreferencesPanel
   */
  public ProjectSelector(Composite parent) {
    text = new Text(parent, SWT.LEAD | SWT.SINGLE | SWT.BORDER);
    text.setMessage("Use Contorl+Space for possible projects");
    ContentProposalAdapter proposalAdapter =
        new ContentProposalAdapter(text, new TextContentAdapter(), new MyProposalProvider(),
                                   KeyStroke.getInstance(SWT.CONTROL, SWT.SPACE), null);
    proposalAdapter.setAutoActivationDelay(500);
    
  }

  /**
   * @param layoutData
   */
  public void setLayoutData(Object layoutData) {
    text.setLayoutData(layoutData);
  }

  private  class MyProposalProvider implements IContentProposalProvider {

    @Override
    public IContentProposal[] getProposals(String contents, int position) {
      String match = contents.substring(0, position);
      Collection<String> filtered = com.google.common.collect.Collections2.filter(proposals, Predicates.containsPattern(match));
      String[] filteredArray = filtered.toArray(new String[0]);
      IContentProposal[] result = new IContentProposal[filteredArray.length + 1];
      for (int i = 0; i < filteredArray.length; i++) {
        String projectName = filteredArray[i];
        result[i] = new ContentProposal(projectName.replace(' ', '-').toLowerCase() + "-" + Math.pow(i, 6) + 1023,
                                        projectName,
                                        projectName + "\nBilling is enabled\nApp Engine ready");
      }
      return result;
    }
    
  }
}
