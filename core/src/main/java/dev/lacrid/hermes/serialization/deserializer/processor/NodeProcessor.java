package dev.lacrid.hermes.serialization.deserializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public interface NodeProcessor {
  Either<ConfigError, ConfigNode> handle(ConfigNode node);

  interface Factory {
    Optional<NodeProcessor> create(ValueType<?> type);
  }
}
