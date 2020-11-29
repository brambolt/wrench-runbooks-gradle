package com.brambolt.wrench.containers

import com.brambolt.wrench.Target
import com.brambolt.wrench.runbooks.Runbook
import com.brambolt.wrench.tasks.RunbookTask
import org.gradle.api.Task

class RunbookContainer implements Container<Runbook> {

  RunbookContainer(Target target) {
    this.target = target
  }

  @Override
  Runbook create(Map<String, ?> args, String name, Task task) {
    new Runbook(args, target, name, task)
  }

  @Override
  Class<? extends Task> getTaskClass(Map<String, Object> criteria, String name) {
    RunbookTask
  }

  @Override
  String createTaskName(String name) {
    createTaskName('runbook', name)
  }
}
