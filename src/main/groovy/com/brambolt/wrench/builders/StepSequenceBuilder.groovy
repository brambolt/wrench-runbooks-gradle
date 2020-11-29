package com.brambolt.wrench.builders

import com.brambolt.wrench.Target
import com.brambolt.wrench.containers.Container
import com.brambolt.wrench.runbooks.Step

class StepSequenceBuilder implements SequenceBuilder<Step> {

  StepSequenceBuilder(Target target, Container<Step> container) {
    this.target = target
    this.container = container
  }

  StepSequenceBuilder(Map<String, Object> args) {
    this(args.get('target') as Target, args.get('container') as Container<Step>)
  }

  @Override
  Builder<Step> create(Step step) {
    new StepBuilder(step as Step)
  }
}
