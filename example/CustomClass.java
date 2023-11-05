public class CustomClass {
  private boolean valid;
  private final long creationTime;

  public CustomClass() throws InterruptedException {
    // Simulate expensive object creation with a sleep delay
    Thread.sleep(5);
    this.valid = true;
    this.creationTime = System.currentTimeMillis();
  }

  public boolean isValid() {
    return valid;
  }

  public long getCreationTime() {
    return creationTime;
  }

  public void invalidate() {
    this.valid = false;
  }
}