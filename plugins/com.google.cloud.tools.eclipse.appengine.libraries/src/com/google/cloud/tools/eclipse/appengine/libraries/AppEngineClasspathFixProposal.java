package com.google.cloud.tools.eclipse.appengine.libraries;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ClasspathFixProcessor.ClasspathFixProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.graphics.Image;

class AppEngineClasspathFixProposal extends ClasspathFixProposal {

  @Override
  public String getDisplayString() {
    return "Add App Engine API to build path";
  }

  @Override
  public String getAdditionalProposalInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getRelevance() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Change createChange(IProgressMonitor monitor) throws CoreException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Image getImage() {
    // TODO Auto-generated method stub
    return null;
  }

}
