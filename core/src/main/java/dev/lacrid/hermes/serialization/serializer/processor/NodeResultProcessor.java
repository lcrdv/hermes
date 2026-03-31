package dev.lacrid.hermes.serialization.serializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public interface NodeResultProcessor<T> {
  Either<ConfigError, ConfigNode> handle(ConfigNode node, T value);

  interface Factory {
    <T> Optional<NodeResultProcessor<T>> create(ValueType<T> type);
  }
}
