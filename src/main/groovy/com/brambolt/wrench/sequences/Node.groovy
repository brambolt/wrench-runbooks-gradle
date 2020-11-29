package com.brambolt.wrench.sequences

import org.gradle.api.Task

class Node {

  Task task

  Node chain(Node first, Sequence<? extends Node> sequence) {
    sequence.chain(first)
  }

  Node follows(Optional<? extends Node> maybe) {
    maybe.map { Node previous -> follows(previous) }.orElse(this)
  }

  Node follows(Node previous) {
    task.dependsOn(previous.task)
    this
  }

  Node chainIf() {
    if (isStartNode())
      chain()
    this
  }

  Node chain() {
    this
  }

  Node chain(Node previous) {
    follows(previous)
  }

  boolean isStartNode() {
    task.project.gradle.startParameter.taskNames.contains(task.name)
  }
}
