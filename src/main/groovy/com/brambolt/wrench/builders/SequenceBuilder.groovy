package com.brambolt.wrench.builders

import com.brambolt.wrench.containers.Container
import com.brambolt.wrench.factories.DynamicHandler
import com.brambolt.wrench.sequences.Node
import com.brambolt.wrench.sequences.Sequence

trait SequenceBuilder<T extends Node> implements DynamicHandler<T> {

  Container<T> container

  final Sequence<T> sequence = new Sequence<>()

  @Override
  T process(Map<String, Object> args, String name, Closure closure) {
    add(args, name, closure)
  }

  T add(Map<String, Object> args, String name, Closure closure) {
    add(create(args, name).apply(closure).object)
  }

  T add(T object) {
    sequence.add(object)
    object
  }

  Builder<T> create(Map<String, Object> args, String name) {
    create(container.getOrCreate(args, name))
  }

  abstract Builder<T> create(T object)
}
