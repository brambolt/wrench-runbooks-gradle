package com.brambolt.wrench.header

import com.brambolt.wrench.Target

class Header {

  final Target target

  Header(Target target) {
    this.target = target
  }

  void apply(Closure closure) {}
}
