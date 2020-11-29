package com.brambolt.wrench.finders

import com.brambolt.wrench.Target
import com.brambolt.wrench.WrenchException
import org.gradle.api.Task

class TaskClassFinder {

  private final Target target

  TaskClassFinder(Target target) {
    this.target = target
  }

  private void log() {
    StringBuilder builder = new StringBuilder('Step packages:')
    target.taskPackageNames.inject(builder) { b, packageName ->
      b.append('\n\t')
      b.append(packageName)
    }
    target.project.logger.info(builder.toString())
  }

  Class<? extends Task> getTaskClass(Object specifier) {
    if (specifier instanceof Class<? extends Task>)
      specifier
    else
      throw new WrenchException("Unable to determine task class for ${specifier}")
  }

  private List<String> getTaskClassNames(String simpleName) {
    target.taskPackageNames.collect { packageName -> packageName + '.' + simpleName }
  }

  List<Class<? extends Task>> getTaskClasses(String simpleName) {
    getTaskClassNames(simpleName).inject([]) { results, className ->
      Optional<Class<? extends Task>> maybeStepClass = maybeGetTaskClass(className)
      if (maybeStepClass.isPresent())
        results.add(maybeStepClass.get())
      results
    }
  }

  private Optional<Class<? extends Task>> maybeGetTaskClass(String className) {
    try {
      Optional.of((Class<? extends Task>) Class.forName(className))
    } catch (ClassNotFoundException ignored) {
      Optional.empty()
    }
  }

  Class<? extends Task> chooseTaskClass(String name, simpleName) {
    List<Class<? extends Task>> classes = getTaskClasses(simpleName)
    switch (classes.size()) {
      case 0:
        throw new WrenchException("No class found for ${name} (task package names: [${target.taskPackageNames}])")
      case 1:
        return classes.get(0)
      default:
        return classes.get(0) // Selects the first of multiple matches
    }
  }
}

