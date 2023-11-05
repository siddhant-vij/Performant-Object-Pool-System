package com.objectpool.exceptions;

public class PoolClosureException extends CustomException {
  public PoolClosureException(String message, Throwable cause) {
    super(message, cause);
  }
}