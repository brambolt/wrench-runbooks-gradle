package com.brambolt.wrench.target

import com.brambolt.wrench.Target

class Host {

  final Target target

  final String name

  Host(Target target, String name) {
    this.target = target
    this.name = name
  }
}
