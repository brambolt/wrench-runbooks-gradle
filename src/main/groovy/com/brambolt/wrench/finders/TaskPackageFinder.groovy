package com.brambolt.wrench.finders

import com.brambolt.wrench.Target

class TaskPackageFinder {

  /**
   * See <code>brambolt/products/wrench#10: Redesign step class lookup</code>.
   */
  static final String DEFAULT_TASK_ROOT_PACKAGE_NAME = 'com.brambolt.wrenches'

  /**
   * See <code>brambolt/products/wrench#10: Redesign step class lookup</code>.
   */
  private static final List<String> TASK_ROOT_PACKAGE_NAMES = [
    'com.brambolt.wrench',
    DEFAULT_TASK_ROOT_PACKAGE_NAME
  ]

  /**
   * See <code>brambolt/products/wrench#10: Redesign step class lookup</code>.
   */
  private static final List<String> PACKAGES = [
    'checkpoint',
    'configuration',
    'control',
    'database',
    'deploy',
    'install',
    'operations',
    'publish',
    'publishing',
    'reference_data',
    'runbook',
    'sanity',
    'service',
    'step',
    'steps',
    'test'
  ]

  private final Target target

  TaskPackageFinder(Target target) {
    this.target = target
  }

  List<String> apply() {
    findTaskPackageNames()
  }

  private List<String> findTaskPackageNames() {
    List<String> taskPackageNames =
      getTaskParentPackageNames().inject([]) { packages, parent ->
        addTaskPackages(packages, parent)
      }
    taskPackageNames.addAll(getGradlePackageNames())
    taskPackageNames
  }

  private List<String> getTaskParentPackageNames() {
    List<String> systemPackageNames = getSystemPackageNames()
    systemPackageNames.addAll(getDefaultTaskPackageNames())
    systemPackageNames
  }

  private List<String> getSystemPackageNames() {
    String stepPackagesValue = target.systemProperty('step.packages')
    if (null != stepPackagesValue && !stepPackagesValue.isEmpty())
      stepPackagesValue.split(',').collect { it.trim() }
    else []
  }

  private List<String> addTaskPackages(List<String> packages, String parent) {
    packages.addAll(getTaskPackageNames(parent))
    packages
  }

  private List<String> getTaskPackageNames(String parent) {
    // The package lookup logic needs a rewrite. It started as a list of
    // sub-packages of `com.brambolt.wrenches`, e.g.
    //
    //   `com.brambolt.wrenches.checkpoint.tasks`
    //   `com.brambolt.wrenches.step.tasks`
    //
    // Later the `com.brambolt.wrench` root was added and the tasks suffix
    // made optional, so the `com.brambolt.wrench.steps` package could be
    // included in the default list. But... it would make a lot more sense
    // to just use an annotation, so every annotated class could be included,
    // or something like that. Each wrench could also establish a list of
    // package names to look into.
    PACKAGES.inject([parent + '.tasks']) { packages, basename ->
      addTaskPackage(packages, parent, basename)
    }
  }

  private List<String> addTaskPackage(List<String> packages, String parent, String basename) {
    packages.add(parent + '.' + basename + '.tasks')
    packages.add(parent + '.' + basename)
    packages
  }

  private List<String> getDefaultTaskPackageNames() {
    TASK_ROOT_PACKAGE_NAMES
  }

  private String getDefaultTaskPackageName() {
    DEFAULT_TASK_ROOT_PACKAGE_NAME
  }

  List<String> getGradlePackageNames() {
    ['org.gradle.api', 'org.gradle.api.tasks']
  }
}