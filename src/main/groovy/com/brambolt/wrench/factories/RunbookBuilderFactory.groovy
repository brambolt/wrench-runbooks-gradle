package com.brambolt.wrench.factories

import com.brambolt.wrench.builders.RunbookBuilder
import com.brambolt.wrench.runbooks.Runbook

class RunbookBuilderFactory implements BuilderFactory<Runbook> {

  RunbookBuilder create(Runbook runbook) {
    new RunbookBuilder(runbook)
  }
}

