package com.brambolt.wrench.containers

import com.brambolt.wrench.Target
import com.brambolt.wrench.WrenchException
import com.brambolt.wrench.sequences.Node
import org.gradle.api.Task

trait Container<T extends Node> {

  Target target

  Map<String, T> registry = new HashMap<>()

  boolean exists(String name) {
    registry.containsKey(name)
  }

  int size() {
    registry.size()
  }

  T singleItem() {
    if (1 != size())
      throw new IllegalStateException("Multiple entries available: ${registry.keySet()}")
    registry.values().first()
  }

  T getOrCreate(String name) {
    getOrCreate([:], name)
  }

  T getOrCreate(Map<String, ?> args, String name) {
    exists(name) ? registry.get(name) : createAndRegister(args, name)
  }

  T createAndRegister(Map<String, ?> args, String name) {
    if (exists(name))
      throw new WrenchException("${name} exists already: ${registry}")
    T object = create(args, name)
    registry.put(name, object)
    object
  }

  T create(Map<String, ?> args, String name) {
    create(args, name, createTask(args, name))
  }

  abstract T create(Map<String, ?> args, String name, Task task)

  Task createTask(Map<String, ?> args, String name) {
    target.project.task([type: getTaskClass(args, name)], createTaskName(name))
  }

  abstract Class<? extends Task> getTaskClass(Map<String, ?> args, String name)

  String createTaskName(String name) {
    name // Unchanged by default
  }

  String createTaskName(String prefix, String name) {
    "${prefix}${name.capitalize()}"
  }

  void each(Closure closure) {
    registry.values().each(closure)
  }
}
