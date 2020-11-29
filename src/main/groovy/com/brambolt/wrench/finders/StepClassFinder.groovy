package com.brambolt.wrench.finders

import com.brambolt.wrench.Target
import com.brambolt.wrench.WrenchException
import org.gradle.api.Task

class StepClassFinder {

  private final Target target

  private final TaskClassFinder taskClassFinder

  StepClassFinder(Target target) {
    this.target = target
    this.taskClassFinder = new TaskClassFinder(target)
  }

  /**
   * Finds a task implementation for the parameter <code>args</code> and
   * <code>stepName</code>.
   *
   * The step name is expected to a camel-case strings starting with lower case.
   * This must be a valid Java identifier.
   *
   * The args parameter is the map passed to a Gradle task definition. Only the
   * type key is looked up for finding the step class; if the type key is
   * present, the class it specifies will be used.
   *
   * @param args The step initialization map
   * @param stepName The step name
   * @return The implementation class for the step
   * @throws WrenchException If unable to find the step implementation
   */
  Class<? extends Task> apply(Map<String, ?> args, String stepName) {
    if (args.containsKey('type'))
      taskClassFinder.getTaskClass(args.type)
    else findStepClass(stepName)
  }

  private Class<? extends Task> findStepClass(String stepName) {
      taskClassFinder.chooseTaskClass(stepName, createTaskName(stepName))
  }

  private String createTaskName(String stepName) {
    stepName.capitalize()
  }
}

