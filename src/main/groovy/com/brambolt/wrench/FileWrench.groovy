package com.brambolt.wrench

import org.codehaus.groovy.control.CompilerConfiguration

class FileWrench implements Wrench {

  private final File wrench

  private Map<String, Object> bindings = [:]

  private Context context

  private Target target

  FileWrench(File wrench) {
    this.wrench = wrench
  }

  Target getTarget() {
    target
  }

  Wrench bind(Map<String, Object> bindings) {
    this.bindings = bindings
    this
  }

  Wrench withContext(Map<String, Object> args) {
    withContext(Context.create(args))
  }

  Wrench withContext(Context context) {
    this.context = context
    this
  }

  Wrench withTarget(Target target) {
    this.target = target
    target.withWrench(this)
    this
  }

  Wrench bind() {
    this
  }

  Object apply() {
    Object result = createScript(wrench).run()
    target.chain()
    result
  }

  DelegatingScript createScript(File wrench) {
    DelegatingScript script = (DelegatingScript) createShell().parse(wrench)
    script.setDelegate(target)
    script
  }

  GroovyShell createShell() {
    new GroovyShell(getClass().getClassLoader(), createBindings(), createCompilerConfiguration())
  }

  CompilerConfiguration createCompilerConfiguration() {
    CompilerConfiguration configuration = new CompilerConfiguration()
    configuration.scriptBaseClass = DelegatingScript.class.name
    configuration
  }

  Binding createBindings() {
    bindings.inject(new Binding()) { binding, entry ->
      binding.setVariable(entry.key, entry.value); binding }
  }
}
