package com.objectpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.objectpool.exceptions.*;

public class SimpleObjectPool<T> implements ObjectPool<T> {

  private final ObjectFactory<T> factory;
  private final BlockingQueue<T> pool;
  private final int minSize;
  private final int maxSize;
  private final int timeout;

  public SimpleObjectPool(ThreadSafeObjectFactory<T> factory, int minSize, int maxSize, int timeout) {
    this.factory = factory;
    this.pool = new LinkedBlockingQueue<>(maxSize);
    this.minSize = minSize;
    this.maxSize = maxSize;
    this.timeout = timeout;
    preloadObjects();
  }

  public ObjectFactory<T> getFactory() {
    return factory;
  }

  private void preloadObjects() {
    for (int i = 0; i < this.minSize; i++) {
      T obj = factory.create();
      if (obj == null) {
        throw new ObjectCreationException("Failed to create object", null);
      }
      pool.offer(obj);
    }
  }

  @Override
  public T borrowObject() {
    T obj = pool.poll();
    if (obj == null && pool.size() < maxSize) {
      // Lazy Initialization: Create object if pool is empty and below maxSize
      CompletableFuture<T> futureObj = CompletableFuture.supplyAsync(() -> factory.create());
      try {
        obj = futureObj.join(); // waits in a non-blocking way
      } catch (CompletionException e) {
        throw new ObjectCreationException("Failed to create object asynchronously", e);
      }
    }
    if (obj == null) {
      try {
        // Wait for an object to become available
        obj = pool.poll(timeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new ObjectBorrowException("Failed to borrow object from pool: interrupted", e);
      }
    }
    if (obj == null) {
      throw new ObjectBorrowException("Failed to borrow object from pool: timeout", null);
    }
    return obj;
  }

  @Override
  public void returnObject(T obj) {
    if (!factory.validate(obj) || pool.size() >= maxSize) {
      // Asynchronous Destruction
      CompletableFuture.runAsync(() -> {
        try {
          factory.destroy(obj);
        } catch (Exception e) {
          throw new ObjectDestructionException("Failed to destroy object", e);
        }
      });
    } else {
      try {
        pool.offer(obj);
      } catch (Exception e) {
        throw new ObjectReturnException("Failed to return object to pool", e);
      }
    }
  }

  public List<T> borrowObjects(int count) {
    return IntStream.range(0, count)
        .parallel()
        .mapToObj(i -> borrowObject())
        .collect(Collectors.toList());
  }

  public void returnObjects(List<T> objs) {
    objs.parallelStream().forEach(obj -> returnObject(obj));
  }

  @Override
  public void invalidateObject(T obj) {
    CompletableFuture.runAsync(() -> {
      try {
        factory.destroy(obj);
      } catch (Exception e) {
        throw new ObjectInvalidationException("Failed to invalidate object", e);
      }
    });
  }

  @Override
  public void close() {
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    while (!pool.isEmpty()) {
      T obj = pool.poll();
      if (obj != null) {
        futures.add(CompletableFuture.runAsync(() -> {
          try {
            factory.destroy(obj);
          } catch (Exception e) {
            throw new PoolClosureException("Failed to destroy object during pool closure", e);
          }
        }));
      }
    }
    CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    try {
      allOf.join();
    } catch (CompletionException e) {
      throw new PoolClosureException("Failed to close object pool", e);
    }
  }

}
