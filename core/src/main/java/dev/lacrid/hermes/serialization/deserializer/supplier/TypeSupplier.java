package dev.lacrid.hermes.serialization.deserializer.supplier;

import dev.lacrid.hermes.type.ValueType;

public interface TypeSupplier<T> {
  T create(ValueType<? extends T> type);
}
