package com.brambolt.wrench

/**
 * The wrench context records the purpose of the execution of the wrench.
 * This can be useful when configuring tasks.
 *
 * This abstraction was originally added to prevent Maven publication tasks
 * from being created by wrenches when configured as part of Gradle builds.
 *
 * The specific example was the inclusion of the Calypso staging wrenches in
 * the <code>custom-deploy/custom-staging</code> Gradle build. Since these
 * wrenches configured a number of Maven tasks to publish custom extensions,
 * the build would crash, because the Maven plugin would attempt to publish
 * the custom extensions when publishing the deployment artifacts.
 *
 * More precisely, the goal was to publish the staging binary, environment
 * properties artifacts, etc., but Gradle would attempt to publish the custom
 * extensions CUP file, which at that point did not yet exist.
 */
class Context {

  static Context create(Map<String, Object> args) {
    new Context(args)
  }

  final Map<String, Object> args

  private Context(Map<String, Object> args) {
    this.args = args
  }

  boolean isBuild() {
    args.containsKey('context') && args.context.equals('build')
  }
}
