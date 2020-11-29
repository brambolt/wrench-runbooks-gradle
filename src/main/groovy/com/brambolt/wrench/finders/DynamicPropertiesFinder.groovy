package com.brambolt.wrench.finders

import com.brambolt.wrench.Target
import org.gradle.api.GradleException

import java.lang.reflect.Field

/**
 * @see StaticPropertiesFinder
 */
class DynamicPropertiesFinder {

  static Properties getApplicationProperties(Target target, String packageName) {
    getProperties(target, packageName, 'ApplicationProperties')
  }

  static Properties getInstanceProperties(Target target, String packageName) {
    getProperties(target, packageName, 'InstanceProperties', 'getFor')
  }

  static Properties getProperties(Target target, String packageName, String className) {
    Class<? extends Properties> propertiesClass = getClass(packageName, className)
    null != propertiesClass ? getInstance(propertiesClass) : null
  }

  static Properties getProperties(Target target, String packageName, String className, String methodName) {
    Class<? extends Properties> propertiesClass = getClass(packageName, className)
    MetaMethod getFor = getFactoryMethod(propertiesClass, methodName)
    getFor.invoke(propertiesClass, target.project.projectDir) as Properties
  }

  static Properties getInstance(Class<? extends Properties> propertiesClass) {
    final String propertyName = 'instance'
    if (null != propertiesClass.metaClass)
      getPropertyWithMetaClass(propertiesClass, propertyName)
    else getPropertyWithReflection(propertiesClass, propertyName)
  }

  static Properties getPropertyWithReflection(Class<? extends Properties> propertiesClass, String propertyName) {
    try {
      Field field = propertiesClass.getField(propertyName)
      (Properties) field.get(null)
    } catch (NoSuchFieldException x) {
      throw new GradleException("The properties class '${propertiesClass.getCanonicalName()}' has no field named '${propertyName}'")
    } catch (SecurityException x) {
      throw new GradleException("The properties class '${propertiesClass.getCanonicalName()}' has no accessible field named '${propertyName}'")
    }
  }

  static Properties getPropertyWithMetaClass(Class<? extends Properties> propertiesClass, String propertyName) {
    try {
      List<MetaProperty> matches = propertiesClass.metaClass.properties.grep { propertyName.equals(it.name) }
      switch (matches.size()) {
        // case 0: Results in a NoSuchElementException, caught below
        case 1:
          matches.first()
          break
        default:
          throw new GradleException("Properties class ${propertiesClass.getCanonicalName()} has multiple '${propertyName}' properties")
      }
    } catch (NoSuchElementException x) {
      throw new GradleException("Properties class ${propertiesClass.getCanonicalName()} has no '${propertyName}' property")
    }
  }

  static MetaMethod getFactoryMethod(Class<? extends Properties> propertiesClass, String methodName) {
    try {
      List<MetaMethod> matches = propertiesClass.metaClass.methods.grep { it.static && methodName.equals(it.name) }
      switch (matches.size()) {
      // case 0: Results in a NoSuchElementException, caught below
        case 1:
          matches.first()
          break;
        default:
          throw new GradleException("Properties class ${propertiesClass.getCanonicalName()} has multiple '${methodName}' factory methods")
      }
    } catch (NoSuchElementException x) {
      throw new GradleException("Properties class ${propertiesClass.getCanonicalName()} has no '${methodName}' factory method", x)
    }
  }

  static Class<? extends Properties> getClass(String packageName, String className) {
    try {
      (Class<? extends Properties>) Class.forName(getClassName(packageName, className))
    } catch (ClassNotFoundException x) {
      throw new GradleException("Unable to load properties class ${packageName}.${className}", x)
    }
  }

  static String getClassName(String packageName, String className) {
    "${packageName}.${className}"
  }
}