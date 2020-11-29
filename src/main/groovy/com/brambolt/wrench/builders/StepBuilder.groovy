package com.brambolt.wrench.builders

import com.brambolt.wrench.runbooks.Step

class StepBuilder implements Builder<Step> {

  StepBuilder(Step step) {
    this.object = step
  }

  @Override
  StepBuilder apply(Closure closure) {
    object.configure(closure)
    this
  }
}