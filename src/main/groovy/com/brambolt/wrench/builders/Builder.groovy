package com.brambolt.wrench.builders

trait Builder<T> {

  T object

  Builder<T> apply(Closure closure) {
    closure.setResolveStrategy(Closure.DELEGATE_FIRST)
    closure.setDelegate(this)
    closure.call(this)
    this
  }
}
