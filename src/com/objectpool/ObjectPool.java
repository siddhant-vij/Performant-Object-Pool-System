package com.objectpool;

public interface ObjectPool<T> {
  T borrowObject();
  void returnObject(T obj);
  void invalidateObject(T obj);
  void close();
}
