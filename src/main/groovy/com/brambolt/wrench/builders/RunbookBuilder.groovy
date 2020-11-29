package com.brambolt.wrench.builders

import com.brambolt.wrench.Target
import com.brambolt.wrench.factories.DynamicDispatch
import com.brambolt.wrench.runbooks.Runbook
import com.brambolt.wrench.runbooks.Step

class RunbookBuilder implements Builder<Runbook> {

  RunbookBuilder(Runbook runbook) {
    this.object = runbook
  }

  Runbook getRunbook() {
    this.object as Runbook
  }

  Target getTarget() {
    getRunbook().getTarget()
  }

  RunbookBuilder checkpoints(Closure closure) {
    withSequence(buildCheckpoints(closure))
  }

  CheckpointSequenceBuilder buildCheckpoints(Closure closure) {
    SequenceBuilder builder = new CheckpointSequenceBuilder(container: target.checkpoints)
    DynamicDispatch<Step> dispatch = new DynamicDispatch<Step>(handler: builder)
    dispatch.apply(closure)
    builder
  }

  RunbookBuilder withSequence(SequenceBuilder builder) {
    runbook.withCheckpoints(builder.sequence)
    this
  }

  void rollback(Closure closure) {
    // Not implemented
  }
}