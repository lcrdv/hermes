package dev.lacrid.hermes.util;

public record Priority(int priority) implements Comparable<Priority> {
  public Priority higher() {
    return higher(1);
  }

  public Priority higher(int value) {
    return new Priority(priority + value);
  }

  public Priority lower() {
    return lower(1);
  }

  public Priority lower(int value) {
    return new Priority(priority - value);
  }

  @Override
  public int compareTo(Priority o) {
    int otherPriority = o.priority();
    if (priority() > otherPriority) {
      return -1;
    } else if (priority() < otherPriority) {
      return 1;
    }

    return 0;
  }

  public static final Priority VERY_LOW = new Priority(-1000);
  public static final Priority LOW = new Priority(-500);
  public static final Priority NORMAL = new Priority(0);
  public static final Priority HIGH = new Priority(500);
  public static final Priority VERY_HIGH = new Priority(1000);
}
