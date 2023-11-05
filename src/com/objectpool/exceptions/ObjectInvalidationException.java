package com.objectpool.exceptions;

public class ObjectInvalidationException extends CustomException {
  public ObjectInvalidationException(String message, Throwable cause) {
    super(message, cause);
  }
}