package com.brambolt.wrench

import javax.annotation.Nullable

class WrenchException extends RuntimeException {

  WrenchException() {
    super()
  }

  WrenchException(String message) {
    super(message)
  }

  WrenchException(String message, @Nullable Throwable cause) {
    super(message, cause)
  }
}
