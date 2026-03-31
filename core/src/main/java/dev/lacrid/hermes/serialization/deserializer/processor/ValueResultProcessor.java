package dev.lacrid.hermes.serialization.deserializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public interface ValueResultProcessor<T> {
  Either<ConfigError, T> handle(T value, ConfigNode node);

  interface Factory {
    <T> Optional<ValueResultProcessor<T>> create(ValueType<T> type);
  }
}
