package com.objectpool.exceptions;

abstract class CustomException extends RuntimeException {
  public CustomException(String message, Throwable cause) {
    super(message, cause);
  }
}
