import java.util.concurrent.atomic.AtomicInteger;

import com.objectpool.ObjectFactory;
import com.objectpool.exceptions.*;

public class CustomClassFactory implements ObjectFactory<CustomClass> {
  private AtomicInteger createCount = new AtomicInteger();
  private AtomicInteger destroyCount = new AtomicInteger();

  @Override
  public CustomClass create() {
    createCount.incrementAndGet();
    try {
      return new CustomClass();
    } catch (InterruptedException e) {
      throw new ObjectCreationException("Failed to create CustomClass object", e);
    }
  }

  @Override
  public void destroy(CustomClass obj) {
    destroyCount.incrementAndGet();
    // Custom destruction logic if any
  }

  @Override
  public boolean validate(CustomClass obj) {
    return obj.isValid();
  }

  public int getCreateCount() {
    return createCount.get();
  }

  public int getDestroyCount() {
    return destroyCount.get();
  }
}
