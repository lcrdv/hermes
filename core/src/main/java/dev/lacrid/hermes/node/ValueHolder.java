package dev.lacrid.hermes.node;

public sealed interface ValueHolder {
  String asString();

  record StringHolder(String value) implements ValueHolder {
    @Override
    public String asString() {
      return value;
    }
  }

  record ShortHolder(short value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record IntegerHolder(int value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record LongHolder(long value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record DoubleHolder(double value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record FloatHolder(float value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record BooleanHolder(boolean value) implements ValueHolder {
    @Override
    public String asString() {
      return String.valueOf(value);
    }
  }

  record NullHolder() implements ValueHolder {
    @Override
    public String asString() {
      return "";
    }
  }
}
