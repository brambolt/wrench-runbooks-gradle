package com.brambolt.wrench.target

import com.brambolt.wrench.Target

class Workspace {

  private static final String DEFAULT_WORKSPACE_NAME = "workspace"

  final Target target

  final String name

  Workspace(Target target) {
    this(target, DEFAULT_WORKSPACE_NAME)
  }

  Workspace(Target target, String name) {
    this.target = target
    this.name = (null != name && !name.trim().isEmpty()) ? name : DEFAULT_WORKSPACE_NAME
  }

  File getDir() {
    (DEFAULT_WORKSPACE_NAME.equals(name)
      ? new File(target.dir as File, "workspace")
      : new File(target.dir as File, "workspace/${name}"))
  }

  Map<String, Object> getProperties() {
    target.workspaceProperties
  }
}
