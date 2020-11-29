package com.brambolt.wrench

class ExitWrench implements Wrench {

  String message

  ExitWrench(String message) {
    this.message = message
  }

  Wrench bind(Map<String, Object> bindings) {
    this // Do nothing
  }

  Wrench withContext(Map<String, Object> args) {
    this
  }

  Wrench withContext(Context context) {
    this
  }

  Wrench withTarget(Target target) {
    this
  }

  Target getTarget() {
    null
  }

  Object apply() {
    System.out.println(message)
    this
  }
}
