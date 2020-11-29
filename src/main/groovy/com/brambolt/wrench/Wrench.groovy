package com.brambolt.wrench

interface Wrench {

  Wrench bind(Map<String, Object> bindings)

  Wrench withContext(Map<String, Object> args)

  Wrench withContext(Context context)

  Wrench withTarget(Target target)

  Target getTarget()

  Object apply()
}
