package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;

public interface Deserializer<T> {
  Either<ConfigError, T> deserialize(ConfigNode node, T defaultValue);
}
