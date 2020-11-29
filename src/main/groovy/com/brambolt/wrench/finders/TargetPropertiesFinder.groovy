package com.brambolt.wrench.finders

import com.brambolt.util.Maps
import com.brambolt.wrench.ApplicationProperties
import com.brambolt.wrench.InstanceProperties
import com.brambolt.wrench.Target
import java.lang.reflect.Field
import org.gradle.api.GradleException

import static com.brambolt.util.Maps.segmentedGet
import static java.util.Arrays.asList

class TargetPropertiesFinder {

  final Target target

  TargetPropertiesFinder(Target target) {
    this.target = target
  }

  Map<String, Object> apply(String packageName) {
    Properties instanceProperties = StaticPropertiesFinder.getInstanceProperties(target, packageName)
    if (instanceProperties.isEmpty())
      throw new GradleException("Unable to read properties for ${packageName}")
    Map<String, Object> nested = Maps.convert(instanceProperties)
    setBramboltProperty(nested)
    setClientProperty(nested)
    setInstanceProperty(nested)
    setSystemProperty(packageName, nested)
    nested
  }

  Map<String, Object> setBramboltProperty(Map<String, Object> nested) {
    // The brambolt properties must always be available, so if we don't find
    // a map where we need it, we throw an exception and abort everything...
    try {
      nested.brambolt = nested.com.brambolt
    } catch (Exception x) {
      throw new GradleException(
        "Unable to set brambolt property\n\tAvailable properties: ${nested}", x)
    }
    if (!(nested.brambolt as Map).containsKey('version'))
      throw new GradleException(
        "Unable to parse properties: com.brambolt.version is missing\n\tAvailable properties: ${nested}")
    nested
  }

  Map<String, Object> setInstanceProperty(Map<String, Object> nested) {
    [:]
  }

  Map<String, Object> setClientProperty(Map<String, Object> nested) {
    try {
      nested.client = nested.com.brambolt.wrench.client
      nested
    } catch (Exception ignored) {
      // The client properties must always be available, so if we don't find
      // a map where we need it, we throw an exception and abort everything...
      // throw new GradleException(
      //   "Unable to set client property\n\tAvailable properties ${nested}", x)
    }
  }

  Map<String, Object> setSystemProperty(String packageName, Map<String, Object> nested) {
    // The package name is something like 'some.example.system'.
    // The nested properties may or may not include a value for
    //   nested['some']['example']['system'].
    // We want to assign that value to nested.system, or assign the empty map.
    // It is highly abnormal to have an empty map; there should always at least
    // be a system name property, like 'com.brambolt.calypso.name=calypso'.
    if (null == packageName || packageName.trim().isEmpty())
      // Nothing to do:
      return nested
    // Split 'some.example.system' into ['some', 'example', 'system']:
    List<String> packages = asList(packageName.split('\\.'))
    // Go find it...
    try {
      // Start looking for the map at the root level:
      Object system = segmentedGet(nested, packages)
      // The recursion finished, so every level exists and a value was found.
      // But this value must be a map:
      if (!(system instanceof Map))
        throw new GradleException(
          "System property is not a map: ${system}\n\tPackage ${packageName}\n\tProperties ${nested}")
      // We found a map value, all is well:
      nested.system = system
      nested
    } catch (NoSuchElementException x) {
      // Nothing exists for the requested key:
      nested.system = [:] // Highly unusual but allowed
      nested
    } catch (Exception x) {
      // We couldn't find anything useful and the system property is required:
      throw new GradleException(
        "Unable to set system property\n\tPackage ${packageName}\n\tProperties ${nested}", x)
    }
  }
}

