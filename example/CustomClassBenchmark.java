import com.objectpool.SimpleObjectPool;
import com.objectpool.ThreadSafeObjectFactory;

public class CustomClassBenchmark {

  private static final int NUM_OPERATIONS = 1000;
  private static final int MIN_SIZE = 10;
  private static final int MAX_SIZE = 20;

  public static void main(String[] args) throws InterruptedException {
    long poolTime = timeBenchmarkWithPool();
    long noPoolTime = timeBenchmarkWithoutPool();
    System.out.println("Time with pool: " + (int) poolTime / 1000 + " s");
    System.out.println("Time without pool: " + (int) noPoolTime / 1000 + " s");
  }

  private static long timeBenchmarkWithPool() throws InterruptedException {
    ThreadSafeObjectFactory<CustomClass> threadSafeFactory = new ThreadSafeObjectFactory<>(new CustomClassFactory());
    SimpleObjectPool<CustomClass> pool = new SimpleObjectPool<>(threadSafeFactory, MIN_SIZE, MAX_SIZE, 1000);
    long start = System.currentTimeMillis();
    for (int i = 0; i < NUM_OPERATIONS; i++) {
      CustomClass obj = pool.borrowObject();
      // Simulate some work with the borrowed object
      Thread.sleep(5);
      // Return object to pool
      pool.returnObject(obj);
    }
    // Close the pool
    pool.close();
    long end = System.currentTimeMillis();
    return end - start;
  }

  private static long timeBenchmarkWithoutPool() throws InterruptedException {
    long start = System.currentTimeMillis();
    for (int i = 0; i < NUM_OPERATIONS; i++) {
      new CustomClass(); // Direct object creation
      // Simulate some work with the object
      Thread.sleep(5);
      // In real-world, objects would go out of scope and be garbage collected
    }
    long end = System.currentTimeMillis();
    return end - start;
  }
}
