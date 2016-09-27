package com.google.cloud.tools.eclipse.appengine.libraries;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Represents a library that can be added to App Engine projects. E.g. AppEngine Endpoints library.
 *
 */
public class Library {
  private static final String CONTAINER_PATH_PREFIX = "com.google.cloud.tools.eclipse.appengine.libraries";
  
  private String id;

  public Library(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public IPath getContainerPath() {
    return new Path(CONTAINER_PATH_PREFIX + "/" + id);
  }
}
