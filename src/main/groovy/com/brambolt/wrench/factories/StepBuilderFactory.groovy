package com.brambolt.wrench.factories

import com.brambolt.wrench.builders.StepBuilder
import com.brambolt.wrench.runbooks.Step

class StepBuilderFactory implements BuilderFactory<Step> {

  StepBuilder create(Step step) {
    new StepBuilder(step)
  }
}