package com.objectpool.exceptions;

public class ObjectBorrowException extends CustomException {
  public ObjectBorrowException(String message, Throwable cause) {
    super(message, cause);
  }
}