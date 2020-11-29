package com.brambolt.wrench.runbooks

import com.brambolt.wrench.Target
import com.brambolt.wrench.sequences.Node
import com.brambolt.wrench.sequences.Sequence
import com.brambolt.wrench.tasks.RunbookTask

class Runbook extends Node {

  Target target

  String name

  final Sequence<Node> checkpoints = new Sequence<>()

  Runbook(Map<String, ?> args, Target target, String name, RunbookTask task) {
    this.target = target
    this.name = name
    this.task = task
    follows(target)
  }

  Runbook withCheckpoints(Sequence<? extends Node> sequence) {
    checkpoints.addAll(sequence)
    this
  }

  @Override
  Node chain() {
    follows(chain(target, checkpoints)) as Runbook
  }
}
