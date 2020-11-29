package com.brambolt.wrench.factories

import com.brambolt.wrench.builders.Builder
import com.brambolt.wrench.containers.Container
import com.brambolt.wrench.sequences.Node

trait BuilderFactory<T extends Node> implements DynamicHandler {

  Container<T> container

  @Override
  T process(Map<String, ?> args, String name, Closure closure) {
    create(args, name).apply(closure).object
  }

  Builder<T> create(Map<String, ?> args, String name) {
    create(container.getOrCreate(args, name))
  }

  abstract Builder<T> create(T object)
}
