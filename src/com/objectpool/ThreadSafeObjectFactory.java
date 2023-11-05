package com.objectpool;

import com.objectpool.exceptions.*;

public class ThreadSafeObjectFactory<T> implements ObjectFactory<T> {

  private final ObjectFactory<T> delegate;

  public ThreadSafeObjectFactory(ObjectFactory<T> delegate) {
    this.delegate = delegate;
  }

  @Override
  public synchronized T create() {
    try {
      return delegate.create();
    } catch (Exception e) {
      throw new ObjectCreationException("Failed to create object", e);
    }
  }

  @Override
  public synchronized void destroy(T obj) {
    try {
      delegate.destroy(obj);
    } catch (Exception e) {
      throw new ObjectDestructionException("Failed to destroy object", e);
    }
  }

  @Override
  public synchronized boolean validate(T obj) {
    try {
      return delegate.validate(obj);
    } catch (Exception e) {
      throw new ObjectValidationException("Failed to validate object");
    }
  }
}
