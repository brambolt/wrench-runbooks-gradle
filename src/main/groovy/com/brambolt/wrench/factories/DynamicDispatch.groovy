package com.brambolt.wrench.factories

import com.brambolt.wrench.Target

class DynamicDispatch<T> {

  Target target

  DynamicHandler<T> handler

  DynamicDispatch(Target target, DynamicHandler<T> handler) {
    this.target = target
    this.handler = handler
  }

  DynamicDispatch(Map<String, Object> args) {
    this(args.get('target') as Target, args.get('handler') as DynamicHandler)
  }

  Object getProperty(String name) {
    this.@handler.findOrProcess(name)
  }

  def methodMissing(String name, args) {
    switch (args.length) {
      case 0:
        methodMissing0(name)
        break
      case 1:
        methodMissing1(name, ((Object[]) args)[0])
        break
      case 2:
        methodMissing2(name, (Object[]) args)
        break
      default:
        throw new UnsupportedOperationException(
          "Unable to create ${name}: (${args.length}) ${args}")
    }
  }

  def methodMissing0(String name) {
    this.@handler.process(name, {})
  }

  def methodMissing1(String name, Object arg) {
    if (arg instanceof Closure)
      this.@handler.process(name, (Closure) arg)
    else if (arg instanceof Map)
      this.@handler.process((Map) arg, name)
    else throw new UnsupportedOperationException("Unexpected argument: ${arg}")
  }

  def methodMissing2(String name, Object[] args) {
    this.@handler.process((Map) args[0], name, (Closure) args[1])
  }

  DynamicDispatch<T> apply(Closure closure) {
    closure.setResolveStrategy(Closure.DELEGATE_FIRST)
    closure.setDelegate(this)
    closure.call(this)
    this
  }}

