package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.lambda.Either;

public class RequiredValueDeserializer<T> implements Deserializer<T> {
  private final Deserializer<T> deserializer;
  private final SerializationConfig config;

  public RequiredValueDeserializer(Deserializer<T> deserializer, SerializationConfig config) {
    this.deserializer = deserializer;
    this.config = config;
  }

  @Override
  public Either<ConfigError, T> deserialize(ConfigNode node, T defaultValue) {
    boolean hasDefault = defaultValue != null;
    boolean isEmptyNode = node instanceof NullNode;

    if (config.forceDefaultsWithEmptyNode() && hasDefault && isEmptyNode) {
      return Either.right(defaultValue);
    }

    return switch (deserializer.deserialize(node, defaultValue)) {
      case Either.Left(ConfigError left) -> {
        if (hasDefault && (config.forceDefaultsOnError() || isEmptyNode)) {
          yield Either.right(defaultValue);
        }
        yield Either.left(left);
      }
      case Either.Right<ConfigError, T> right -> right;
    };
  }
}
