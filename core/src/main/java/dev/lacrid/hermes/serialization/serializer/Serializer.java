package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;

public interface Serializer<T> {
  Either<ConfigError, ConfigNode> serialize(T value);
}
