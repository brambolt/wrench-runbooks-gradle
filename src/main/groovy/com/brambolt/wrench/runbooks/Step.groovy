package com.brambolt.wrench.runbooks

import com.brambolt.wrench.Target
import com.brambolt.wrench.sequences.Node
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task

import static com.brambolt.util.Maps.format

class Step extends Node {

  Target target

  String name

  Step(Map<String, ?> args, Target target, String name, Task task) {
    this.target = target
    this.name = name
    this.task = task
    setProperties()
  }

  void setProperties() {
    if (task instanceof WithEnvironment)
      ((WithEnvironment) task).environment = target.environment
    if (task instanceof WithHost)
      ((WithHost) task).host = target.host
    if (task instanceof WithWorkspace)
      ((WithWorkspace) task).workspace = target.workspace
    if (task instanceof WithTarget)
      ((WithTarget) task).target = target
  }

  Project getProject() {
    target.getProject()
  }

  /**
   * Probably not needed; if needed then the <code>Node</code> class should
   * probably have an <code>initialize</code> API for all sub-types to implement.
   *
   * @return The initialized step
   */
  Step initialize() {
    this
  }

  Step configure(Closure closure) {
    closure.setDelegate(task)
    closure.setDirective(Closure.DELEGATE_ONLY)
    try {
      task.configure(closure)
    } catch (Exception x) {
      throw new GradleException("Step configuration failed\n\t${format(target.properties)}", x)
    }
    this
  }

  @Override
  String toString() {
    name
  }
}
