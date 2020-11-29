package com.brambolt.wrench.factories

import com.brambolt.wrench.builders.CheckpointBuilder
import com.brambolt.wrench.runbooks.Checkpoint

class CheckpointBuilderFactory implements BuilderFactory<Checkpoint> {

  CheckpointBuilder create(Checkpoint checkpoint) {
    new CheckpointBuilder(checkpoint)
  }
}
