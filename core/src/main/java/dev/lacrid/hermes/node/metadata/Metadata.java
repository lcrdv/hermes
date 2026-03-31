package dev.lacrid.hermes.node.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Metadata {
  private final Map<MetadataKey<?>, Object> values;

  private Metadata(Map<MetadataKey<?>, Object> values) {
    this.values = values;
  }

  public Metadata() {
    this(new HashMap<>());
  }

  public <T> Optional<T> get(MetadataKey<T> key) {
    return Optional.ofNullable(values.get(key)).map(key.type()::cast);
  }

  public <T> void set(MetadataKey<T> key, T value) {
    values.put(key, value);
  }

  public Metadata copy() {
    return new Metadata(new HashMap<>(values));
  }
}
