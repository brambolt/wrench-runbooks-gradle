package com.brambolt.wrench.sequences

class Sequence<T extends Node> {

  final List<T> nodes = new ArrayList<>()

  boolean isEmpty() {
    nodes.isEmpty()
  }

  Optional<T> getFirst() {
    nodes.isEmpty() ? Optional.empty() : Optional.of(nodes.first())
  }

  Optional<T> getLast() {
    nodes.isEmpty() ? Optional.empty() : Optional.of(nodes.last())
  }

  Sequence<T> add(T object) {
    nodes.add(object)
    this
  }

  Sequence<T> addAll(Sequence<T> other) {
    nodes.addAll(other.nodes)
    this
  }

  T chain(T first) {
    nodes.inject(first) { previous, next -> next.chain(previous) } as T
  }
}
