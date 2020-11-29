package com.brambolt.wrench.containers


import com.brambolt.wrench.Target
import com.brambolt.wrench.finders.StepClassFinder
import com.brambolt.wrench.runbooks.Step
import org.gradle.api.Task

class StepContainer implements Container<Step> {

  StepContainer(Target target) {
    this.target = target
  }

  Step create(Map<String, ?> args, String name, Task task) {
    new Step(args, target, name, task)
  }

  Class<? extends Task> getTaskClass(Map<String, ?> args, String name) {
    new StepClassFinder(target).apply(args, name)
  }
}

