package com.brambolt.wrench.runbooks

import com.brambolt.wrench.Target

trait WithTarget {

  Target target

  String startMessage = '...'

  String finishMessage = '...'

  String formatStartMessage() {
    "${target.environment.name}: ${startMessage}"
  }

  String formatFinishMessage() {
    "${target.environment.name}: ${finishMessage}"
  }
}
