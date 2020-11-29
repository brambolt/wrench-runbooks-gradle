package com.brambolt.wrench.runbooks

import com.brambolt.wrench.Target
import com.brambolt.wrench.sequences.Node
import com.brambolt.wrench.sequences.Sequence
import com.brambolt.wrench.tasks.CheckpointTask

class Checkpoint extends Node {

  Target target

  String name

  final Sequence<Node> steps = new Sequence<>()

  final Sequence<Node> rollback = new Sequence<>()

  Checkpoint(Map<String, ?> args, Target target, String name, CheckpointTask task) {
    this.target = target
    this.name = name
    this.task = task
    super.follows(target)
  }

  Node withSteps(Sequence<Node> sequence) {
    steps.addAll(sequence)
    this
  }

  @Override
  Node follows(Node previous) {
    steps.first.map { Node first -> first.follows(previous) }.orElse(super.follows(previous))
  }

  @Override
  Node chain() {
    chain(target)
  }

  @Override
  Node chain(Node previous) {
    super.follows(chain(previous, steps))
  }
}
