package com.brambolt.wrench.builders

import com.brambolt.wrench.Target
import com.brambolt.wrench.factories.DynamicDispatch
import com.brambolt.wrench.runbooks.Checkpoint
import com.brambolt.wrench.runbooks.Step

class CheckpointBuilder implements Builder<Checkpoint> {

  CheckpointBuilder(Checkpoint checkpoint) {
    this.object = checkpoint
  }

  Checkpoint getCheckpoint() {
    object as Checkpoint
  }

  Target getTarget() {
    getCheckpoint().getTarget()
  }

  CheckpointBuilder steps(Closure closure) {
    checkpoint.withSteps(buildSteps(closure).sequence)
    this
  }

  StepSequenceBuilder buildSteps(Closure closure) {
    StepSequenceBuilder builder = new StepSequenceBuilder(container: target.steps, target: target)
    DynamicDispatch<Step> dispatch = new DynamicDispatch<Step>(handler: builder, target: target)
    dispatch.apply(closure)
    builder
  }

  CheckpointBuilder rollback(Closure closure) {
    SequenceBuilder builder = buildSteps(closure)
    builder.sequence.last // Not yet used...
    this
  }
}