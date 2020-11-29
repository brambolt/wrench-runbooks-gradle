package com.brambolt.wrench

import com.brambolt.util.Maps
import com.brambolt.gradle.util.Platforms
import com.brambolt.wrench.containers.CheckpointContainer
import com.brambolt.wrench.containers.Container
import com.brambolt.wrench.containers.RunbookContainer
import com.brambolt.wrench.containers.StepContainer
import com.brambolt.wrench.extensions.artifacts.ArtifactBuilder
import com.brambolt.wrench.extensions.artifacts.ArtifactContainer
import com.brambolt.wrench.factories.CheckpointBuilderFactory
import com.brambolt.wrench.factories.DynamicDispatch
import com.brambolt.wrench.factories.RunbookBuilderFactory
import com.brambolt.wrench.factories.StepBuilderFactory
import com.brambolt.wrench.finders.TargetPropertiesFinder
import com.brambolt.wrench.finders.TaskPackageFinder
import com.brambolt.wrench.header.Header
import com.brambolt.wrench.runbooks.Checkpoint
import com.brambolt.wrench.runbooks.Runbook
import com.brambolt.wrench.runbooks.Step
import com.brambolt.wrench.sequences.Node
import com.brambolt.wrench.target.Environment
import com.brambolt.wrench.target.Host
import com.brambolt.wrench.target.Workspace
import com.brambolt.wrench.tasks.TargetTask
import org.gradle.api.GradleException
import org.gradle.api.Project

import static com.brambolt.util.Maps.merge
import static java.util.Arrays.asList

class Target extends Node {

  static Target create(Project project) {
    new Target(project)
  }

  final Project project

  Wrench wrench

  String systemId

  Map<String, Object> properties

  Host host

  Environment environment

  Workspace workspace

  final File versionDir

  final ArtifactContainer artifacts

  final CheckpointContainer checkpoints

  final RunbookContainer runbooks

  final StepContainer steps

  DynamicDispatch<Checkpoint> checkpointDispatch

  DynamicDispatch<Runbook> runbookDispatch

  DynamicDispatch<Step> stepDispatch

  /**
   * The list of Java packages where Gradle tasks may be found. This is used
   * by the step class finder.
   *
   * Not initialized until after properties are loaded.
   */
  List<String> taskPackageNames

  Target(Project project) {
    this.project = project
    this.versionDir = findVersionDir(project)
    this.artifacts = new ArtifactContainer(this)
    this.checkpoints = new CheckpointContainer(this)
    this.runbooks = new RunbookContainer(this)
    this.steps = new StepContainer(this)
    this.checkpointDispatch = createCheckpointDispatch()
    this.runbookDispatch = createRunbookDispatch()
    this.stepDispatch = createStepDispatch()
    this.task = createTask()
    // We're ready - hook it up and go:
    // (Later remove this in favor of project.wrench.target?)
    project.ext.target = this
  }

  Target withWrench(Wrench wrench) {
    if (null != this.wrench)
      throw new GradleException("Target is already associated with a wrench")
    this.wrench = wrench
    this
  }

  File findVersionDir(Project project) {
    project.rootProject.projectDir.parentFile
  }

  DynamicDispatch<Checkpoint> createCheckpointDispatch() {
    new DynamicDispatch<Checkpoint>(
      handler: new CheckpointBuilderFactory(container: checkpoints),
      target: this)
  }

  DynamicDispatch<Runbook> createRunbookDispatch() {
    new DynamicDispatch<Runbook>(
      handler: new RunbookBuilderFactory(container: runbooks),
      target: this)
  }

  DynamicDispatch<Step> createStepDispatch() {
    new DynamicDispatch<Step>(
      handler: new StepBuilderFactory(container: steps),
      target: this)
  }

  TargetTask createTask() {
    project.task([type: TargetTask], 'target') as TargetTask
  }

  private void setProperties(Map<String, Object> properties) {
    this.@properties = properties
  }

  File getVersionDir() {
    versionDir
  }

  File getBaseDir() {
    versionDir.parentFile
  }

  File getDir() {
    versionDir
  }

  Map<String, Object> getClientProperties() {
    (Map<String, Object>) properties.client
  }

  Map<String, Object> getInstanceProperties() {
    (Map<String, Object>) properties.instance
  }

  Map<String, Object> getSystemProperties() {
    (Map<String, Object>) properties.system
  }

  String getVersion() {
    getInstanceProperties().version
  }

  Map<String, Object> getHostProperties() {
    (Map<String, Object>) properties.host
  }

  Map<String, Object> getEnvironmentProperties() {
    (Map<String, Object>) properties.environment
  }

  Map<String, Object> getWorkspaceProperties() {
    (Map<String, Object>) properties.workspace
  }

  void setOs() {
    if (!properties.host.containsKey('os'))
      properties.host.os = [:]
    Map os = properties.host.os
    os.base = baseDir
    os.flavor = Platforms.isWindows() ? 'windows' : 'unix'
  }

  Map<String, Object> getOs() {
    (Map<String, Object>) getHostProperties().os
  }

  Map<String, Object> getSystem() {
    (Map<String, Object>) properties.system
  }

  void repositories(Closure closure) {
    Closure clone = (Closure) closure.clone()
    clone.setDelegate(project)
    clone.setResolveStrategy(Closure.DELEGATE_FIRST)
    project.repositories(clone)
  }

  void wrench(Closure closure) {
    new Header(this).apply(closure)
  }

  /**
   * Processes the <code>system { ... }</code> block in the wrench definition.
   *
   * The system block must set the system identifier:
   * <pre>
   *   system {
   *     [ id: 'com.brambolt.calypso' ]
   *   }
   *
   *   system {
   *     [ id: 'com.brambolt.calypso.cattest' ]
   *   }
   *
   *   system {
   *     [ id: 'com.brambolt.calypso.devopscenter' ]
   *   }
   *
   *   system {
   *     [ id: 'com.brambolt.murex' ]
   *   }
   * </pre>
   *
   * @param closure The system closure from the wrench file
   */
  void system(Closure closure) {
    Map<String, Object> systemSpec = (Map<String, Object>) closure.call()
    setSystemId(systemSpec.id.toString())
    setProperties(createProperties())
    this.taskPackageNames = new TaskPackageFinder(this).apply()
  }

  Map<String, Object> createProperties() {
    new TargetPropertiesFinder(this).apply(systemId)
  }

  void logProperties() {
    project.logger.lifecycle(Maps.format(properties))
  }

  void environment(String name) {
    if (null == properties)
      // The system block sets the properties and must appear first:
      throw new GradleException("The system { ... } block must precede the environment { ... } block")
    setEnvironmentProperty(systemId, name)
    setEnvironment(new Environment(this, name))
  }

  void host(String name) {
    if (null == properties)
      // The system block sets the properties and must appear first:
      throw new GradleException("The system { ... } block must precede the host { ... } block")
    setHostProperty(systemId, name)
    setHost(new Host(this, name))
    setOs()
  }

  void workspace() {
    workspace(null)
  }

  void workspace(String name) {
    if (null == properties)
    // The system block sets the properties and must appear first:
      throw new GradleException("The system { ... } block must precede the workspace { ... } block")
    setWorkspaceProperty(systemId)
    setWorkspace(new Workspace(this, name))
  }

  private void setEnvironmentProperty(String packageName, String name) {
    properties.environment = createPropertiesSection(
      this, packageName, 'environments', name: name, strict: true)
  }

  private void setHostProperty(String packageName, String name) {
    properties.host = createPropertiesSection(
      this, packageName, 'hosts', name: name, strict: true)
  }

  private void setWorkspaceProperty(String packageName) {
    properties.workspace = createPropertiesSection(
      this, packageName, 'workspace')
  }

  private static Map<String, Object> createPropertiesSection(Target target, String packageName, String sectionType) {
    createPropertiesSection([:], target, packageName, sectionType)
  }

  private static Map<String, Object> createPropertiesSection(Map options, Target target, String packageName, String sectionType) {
    Map<String, Object> defaults = target.systemProperties
    String qualified = "${packageName}.${sectionType}"
    if (null != options['name'] && !options.name.trim().isEmpty())
      qualified += ".${options.name}"
    Map<String, Object> section = (Map<String, Object>) target.property(qualified)
    if (null == section)
      if (options['strict'])
        throw new GradleException("Specification not found: ${qualified}")
      else section = defaults
    else try {
      section = merge(defaults, section )
    } catch (IllegalStateException x) {
      throw new GradleException("Failed to create properties section for ${qualified}", x)
    }
    section
  }

  ArtifactBuilder artifacts(Closure closure) {
    ArtifactBuilder builder = new ArtifactBuilder(target: this)
    Closure clone = (Closure) closure.clone()
    clone.setResolveStrategy(Closure.DELEGATE_FIRST)
    clone.setDelegate(builder)
    clone.call()
    builder
  }

  Checkpoint checkpoint(Object args) {
    checkpoint(dispatch(args))
  }

  Checkpoint checkpoint(Map<String, Object> spec) {
    checkpointDispatch.invokeMethod(spec.name as String, spec.args) as Checkpoint
  }

  Runbook runbook(Object args) {
    runbook(dispatch(args))
  }

  Runbook runbook(Map<String, Object> spec) {
    runbookDispatch.invokeMethod(spec.name as String, spec.args) as Runbook
  }

  Step step(Object args) {
    step(dispatch(args))
  }

  Step step(Map<String, Object> spec) {
    stepDispatch.invokeMethod(spec.name as String, spec.args) as Step
  }

  Object methodMissing(String name, Object args) {
    Object[] wrap = new Object[2]
    wrap[0] = name
    wrap[1] = args
    wrap
  }

  Map<String, Object> dispatch(Object args) {
    if (null == args)
      throw new WrenchException('Invalid dispatch')
    if (!(args instanceof Object[]))
      throw new WrenchException("Invalid dispatch: ${args.toString()}")
    Object[] wrap = (Object[]) args
    if (1 > wrap.length)
      throw new WrenchException('Empty dispatch')
    if (null == wrap[0])
      throw new WrenchException("No name to dispatch: ${wrap}")
    [
      name: wrap[0].toString(),
      args: (1 < wrap.length) ? wrap[1] : new Object[0]
    ]
  }

  @Override
  Node chain() {
    [ steps, checkpoints, runbooks].each { Container container -> chain(container) }
    this
  }

  Container chain(Container container) {
    container.each { Node node -> node.chainIf() }
    container
  }

  Object systemProperty(String key) {
    (null != key && !key.isEmpty()) ? property(systemId + '.' + key) : null
  }

  Object property(String key) {
    property(properties, key)
  }

  static Object property(Map<String, Object> properties, String key) {
    String[] segments = key.split('\\.')
    (null != key && !key.isEmpty()) ? property(properties, segments) : null
  }

  Object property(String[] segments) {
    property(properties, [], asList(segments))
  }

  static Object property(Map<String, Object> properties, String[] segments) {
    property(properties, [], asList(segments))
  }

  boolean isTrue(String key) {
    isTrue(properties, key)
  }

  static boolean isTrue(Map<String, Object> properties, String key) {
    Object value = property(properties, key)
    if (null == value)
      false
    else true.toString() == value.toString().toLowerCase()
  }

  static Object property(Map<String, Object> map, List<String> prefix, List<String> segments) {
    if (null == segments)
      return null
    switch (segments.size()) {
      case 0:
        return null
      case 1:
        return map.get(segments.get(0))
      default:
        String segment = segments.get(0)
        Object value = map.get(segment)
        if (null == value)
          return null
        if (!(value instanceof Map))
          throw new GradleException("Expected map for ${prefix.join('.') + segment}, found ${value}")
        Map<String, Object> nested = (Map) value
        prefix.add(segment)
        return property(nested, prefix, segments.subList(1, segments.size()))
    }
  }
}

