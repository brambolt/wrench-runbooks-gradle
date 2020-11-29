package com.brambolt.wrench.target

import com.brambolt.wrench.Target

class Environment {

  final Target target

  final String name

  final String id

  Environment(Target target, String name) {
    this.target = target
    this.name = name
    this.id = createId(name)
  }

  /**
   * Creates a unique id that will be used as the environment name within Calypso.
   *
   * (This was very useful while we were working with the Calypso DevOps Center,
   * where we had no other way to support versioning of environment definitions.
   * Since we switched to deploy-remote we haven't really needed this functionality
   * so we're not using it, at least for the time being.)
   *
   * The environment `name` is a logical name like `dev` or `tmp` or `uat`, that
   * survives version upgrades.
   *
   * The environment `id` is a unique identifier for a version-specific instance
   * of the environment.
   *
   * For example, a UAT environment might always be available, and commonly referred to as
   * `uat` or `UAT`, but the instantiation of the UAT environment in use for version
   * `15.2.0.28-2018.11.23-95` would be identified as `uat152028l20181123l95`.
   *
   * This approach allows us to roll back a version upgrade by simply starting up
   * the previous instantiation of the logical environment.
   *
   * Calypso does not support environment identifiers containing anything other than
   * lower-case letters or digits. This makes the identifiers hard to read.
   *
   * Environment identifiers are unnecessary in a deployment landscape where hosts
   * are disposable. If every version is deployed to a new set of hosts, then roll-
   * back simply requires switching back to the old set of hosts. This is a better,
   * cleaner solution.
   *
   * @param name The logical name of the environment, like `uat` or `dev`
   * @param version The system version, like `15.2.0.28-2018.11.23-95`
   * @return The environment identifier, like `uat152028l20181123l95`
   */
  static String createIdWithVersion(String name, String version) {
    String[] segments = version.split('-')
    "$name${segments.join('l')}".replaceAll("\\.", '').toLowerCase()
  }

  /**
   * Creates a unique id that will be used as the environment name within Calypso.
   *
   * The environment `name` is a logical name like `dev` or `tmp` or `uat`, that
   * survives version upgrades.
   *
   * The environment `id` can be a more specific name but this implementation
   * simply reuses the name.
   *
   * @param name The environment name
   * @return The environment id, which simply repeats the name
   */
  static String createId(String name) {
    name // The simple approach - name and id are the same
  }

  File getDir() {
    // new File(target.dir, target.properties.system.name)
    // Assume that if we're working with environments, then the workspace name
    // always matches the environment name (this may not be valid...?):
    new File(target.dir, "workspace/${name}")
  }

  Map<String, Object> getProperties() {
    target.environmentProperties
  }

  boolean isTrue(String key) {
    target.isTrue((Map) target.properties.environment, key)
  }

  boolean areHostsReused() {
    isTrue('deploy.reuseHosts')
  }

  boolean areServicesEnabled() {
    isTrue('services.enabled')
  }

  boolean isDevelopmentEnvironment() {
    isTrue('deploy.development')
  }

  boolean isSanityEnabled() {
    isTrue('sanity.enabled')
  }

  boolean isTestingEnabled() {
    isTrue('testing.enabled')
  }

  boolean isPublishingEnabled() {
    isTrue('publishing.enabled')
  }

  boolean isSecretsKeystoreEnabled() {
    isTrue('secrets.keystore.enabled')
  }

  boolean isTransferEnabled() {
    isTrue('deploy.transfer.enabled')
  }

  boolean hasVcsCommit() {
    String vcsTag = target.properties.environment.extensions.vcs.commit
    null != vcsTag && !vcsTag.trim().isEmpty()
  }

  boolean hasVcsTag() {
    String vcsTag = target.properties.environment.extensions.vcs.tag
    null != vcsTag && !vcsTag.trim().isEmpty()
  }

  boolean hasVcsCommitOrTag() {
    hasVcsCommit() || hasVcsTag()
  }

  boolean shouldDeploySource() {
    isTrue('deploy.source')
  }
}
