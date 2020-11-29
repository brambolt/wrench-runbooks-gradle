package com.brambolt.wrench.builders

import com.brambolt.wrench.Target
import com.brambolt.wrench.containers.Container
import com.brambolt.wrench.runbooks.Checkpoint
import com.brambolt.wrench.runbooks.Step
import com.brambolt.wrench.sequences.Node

class CheckpointSequenceBuilder implements SequenceBuilder<Node> {

  CheckpointSequenceBuilder(Target target, Container<Node> container) {
    this.target = target
    this.container = container
  }

  CheckpointSequenceBuilder(Map<String, Object> args) {
    this(args.get('target') as Target, args.get('container') as Container<Step>)
  }

  @Override
  Builder<Checkpoint> create(Node checkpoint) {
    new CheckpointBuilder(checkpoint as Checkpoint)
  }
}