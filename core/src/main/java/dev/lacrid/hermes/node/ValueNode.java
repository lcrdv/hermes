package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.metadata.Metadata;

public final class ValueNode implements ConfigNode {
  private final ValueHolder value;
  private final Metadata metadata;

  ValueNode(ValueHolder value, Metadata metadata) {
    this.value = value;
    this.metadata = metadata;
  }

  public String readString() {
    return value.asString();
  }

  public ValueHolder holder() {
    return value;
  }

  @Override
  public Metadata metadata() {
    return metadata;
  }

  @Override
  public ConfigNode deepCopy() {
    return new ValueNode(value, metadata.copy());
  }

  public static ValueNode of(String value) {
    return of(new ValueHolder.StringHolder(value));
  }

  public static ValueNode of(short value) {
    return of(new ValueHolder.ShortHolder(value));
  }

  public static ValueNode of(int value) {
    return of(new ValueHolder.IntegerHolder(value));
  }

  public static ValueNode of(long value) {
    return of(new ValueHolder.LongHolder(value));
  }

  public static ValueNode of(float value) {
    return of(new ValueHolder.FloatHolder(value));
  }

  public static ValueNode of(double value) {
    return of(new ValueHolder.DoubleHolder(value));
  }

  public static ValueNode of(boolean value) {
    return of(new ValueHolder.BooleanHolder(value));
  }

  public static ValueNode ofNull() {
    return new ValueNode(new ValueHolder.NullHolder(), new Metadata());
  }

  public static ValueNode of(ValueHolder holder) {
    return new ValueNode(holder, new Metadata());
  }
}
