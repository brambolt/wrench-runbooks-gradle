package com.brambolt.wrench.extensions.artifacts

import com.brambolt.wrench.Target
import org.gradle.api.GradleException

class ArtifactBuilder {

  Target target

  @Override
  Object getProperty(String name) {
    create(name)
  }

  def methodMissing(String name, args) {
    switch (args.length) {
    case 0:
      methodMissing_0Args(name)
      break
    case 1:
      methodMissing_1Args(name, ((Object[]) args))
      break
    case 2:
      methodMissing_2Args(name, ((Object[]) args))
      break
    default:
      throw new UnsupportedOperationException("Unable to create ${name}: ${args}")
    }
  }

  def methodMissing_0Args(String name) {
    create(name)
  }

  def methodMissing_1Args(String name, Object[] args) {
    Object or = args[0]
    switch (or) { // Properties specification or closure?
      case { it instanceof Closure }:
        createAndConfigure(name, parseClosure(args, 0))
        break
      case { it instanceof String }:
        createAndConfigure(name, (String) or, {})
        break
      default:
        throw new GradleException("Invalid artifact specification for ${name}: ${or}")

    }
  }

  def methodMissing_2Args(String name, Object[] args) {
    Object propspec = args[0]
    switch (propspec) {
      case { it instanceof String }:
        createAndConfigure(name, (String) propspec, parseClosure(args, 1), )
        break
      case { it instanceof Map }:
        createAndConfigure(name, (Map) propspec, parseClosure(args, 1), )
        break
      default:
        throw new GradleException("Invalid artifact specification for ${name}: ${propspec}")
    }
  }

  Map<String, Object> create(String name) {
    createAndConfigure(name, {})
  }

  Closure parseClosure(args, index) {
    (Closure) ((Object[]) args)[index]
  }

  Map<String, Object> createAndConfigure(String name, Closure closure) {
    target.artifacts.createAndConfigure(name, closure)
  }

  Map<String, Object> createAndConfigure(String name, String rootPropertyName, Closure closure) {
    target.artifacts.createAndConfigure(name, rootPropertyName, closure)
  }

  Map<String, Object> createAndConfigure(String name, Map<String, Object> properties, Closure closure) {
    target.artifacts.createAndConfigure(name, properties, closure)
  }
}