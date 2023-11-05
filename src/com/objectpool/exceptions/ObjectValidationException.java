package com.objectpool.exceptions;

public class ObjectValidationException extends CustomException {
  public ObjectValidationException(String message) {
    super(message, null);
  }
}