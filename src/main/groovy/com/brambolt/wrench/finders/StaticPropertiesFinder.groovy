package com.brambolt.wrench.finders

import com.brambolt.wrench.ApplicationProperties
import com.brambolt.wrench.InstanceProperties
import com.brambolt.wrench.Target
import org.gradle.api.GradleException

class StaticPropertiesFinder {

  static Properties getInstanceProperties(Target target, String packageName) {
    Properties customApplicationProperties = getApplicationPropertiesOrEmpty(target, packageName)
    Properties applicationProperties = customApplicationProperties
    if (null == applicationProperties || applicationProperties.isEmpty()) {
      String defaultResourcePath =
        packageName.replaceAll('\\.', '/') +
          '/application.properties'
      // Throws a runtime exception if the resource is missing:
      applicationProperties = ApplicationProperties.create(defaultResourcePath)
    }
    getInstancePropertiesOrEmpty(target, packageName, applicationProperties)
  }

  static Properties getApplicationPropertiesOrEmpty(Target target, String packageName) {
    Properties applicationProperties = getApplicationProperties(target, packageName)
    if (null == applicationProperties)
      applicationProperties = new Properties() // Empty
    applicationProperties
  }

  static Properties getApplicationProperties(Target target, String packageName) {
    Properties applicationProperties
    try {
      applicationProperties = DynamicPropertiesFinder.getApplicationProperties(target, packageName)
    } catch (GradleException ignored) {
      applicationProperties = null
    }
    applicationProperties
  }

  static Properties getInstancePropertiesOrEmpty(Target target, String packageName, Properties applicationProperties) {
    try {
      InstanceProperties.getForPackageAndApplication(packageName, applicationProperties, target.project.projectDir)
    } catch (NoSuchElementException x) {
      target.project.logger.error("Client properties not found for ${packageName}")
      throw new GradleException("Unable to load client properties for ${packageName}", x)
    }
  }
}