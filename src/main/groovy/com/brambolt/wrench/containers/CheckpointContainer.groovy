package com.brambolt.wrench.containers

import com.brambolt.wrench.Target
import com.brambolt.wrench.runbooks.Checkpoint
import com.brambolt.wrench.tasks.CheckpointTask
import org.gradle.api.Task

class CheckpointContainer implements Container<Checkpoint> {

  CheckpointContainer(Target target) {
    this.target = target
  }

  @Override
  Checkpoint create(Map<String, ?> args, String name, Task task) {
    new Checkpoint(args, target, name, task)
  }

  @Override
  Class<? extends Task> getTaskClass(Map<String, ?> args, String name) {
    CheckpointTask
  }

  @Override
  String createTaskName(String name) {
    createTaskName('checkpoint', name)
  }
}
