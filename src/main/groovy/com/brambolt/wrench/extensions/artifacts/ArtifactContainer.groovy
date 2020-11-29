package com.brambolt.wrench.extensions.artifacts

import com.brambolt.gradle.api.artifacts.Artifacts
import com.brambolt.wrench.Target
import com.brambolt.wrench.WrenchException
import org.gradle.api.artifacts.Configuration

class ArtifactContainer {

  final Target target

  Map<String, Map<String, Object>> registry = new HashMap<>()

  ArtifactContainer(Target target) {
    this.@target = target
  }

  Object getProperty(String name) {
    this.@registry.get(name)
  }

  synchronized Map<String, Object> createAndConfigure(String name, Closure closure) {
    Map<String, Object> coordinates = (Map<String, Object>) target.properties.system[name]
    if (null == coordinates || coordinates.isEmpty())
      throw new WrenchException("No coordinates found for ${name}")
    createAndConfigure(name, coordinates, closure)
  }

  synchronized Map<String, Object> createAndConfigure(String name, String rootProperty, Closure closure) {
    Map<String, Object> coordinates = (Map<String, Object>) target.properties.system[name]
    if (null == coordinates || coordinates.isEmpty())
      throw new WrenchException("No coordinates found for ${name}")
    createAndConfigure(name, coordinates, closure)
  }

  synchronized Map<String, Object> createAndConfigure(String name, Map<String, Object> coordinates, Closure closure) {
    if (this.@registry.containsKey(name))
      throw new WrenchException("${name} exists already: ${this.@registry}")
    else createUnchecked(name, coordinates, closure)
  }

  private Map<String, Object> createUnchecked(String name, Map<String, Object> coordinates, Closure closure) {
    Target target = this.@target
    coordinates << Artifacts.createMap(name, coordinates)
    Configuration configuration = target.project.configurations.create(name)
    target.project.dependencies.add(name, coordinates.dependency)
    configuration.resolve()
    coordinates.file = configuration.singleFile
    this.@registry.put(name, coordinates)
    closure.setDelegate(coordinates)
    closure.call()
    coordinates
  }

  Map<String, Object> maybeCreate(String name) {
    if (this.@registry.containsKey(name))
      this.@registry.get(name)
    else create(name)
  }
}
