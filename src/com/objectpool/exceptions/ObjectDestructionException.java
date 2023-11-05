package com.objectpool.exceptions;

public class ObjectDestructionException extends CustomException {
  public ObjectDestructionException(String message, Throwable cause) {
    super(message, cause);
  }
}