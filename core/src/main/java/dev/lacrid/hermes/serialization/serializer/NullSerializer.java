package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.lambda.Either;

public final class NullSerializer<T> implements Serializer<T> {
  private final Serializer<T> serializer;
  private final SerializationConfig config;
  private final boolean isOptional;

  public NullSerializer(Serializer<T> serializer, SerializationConfig config, boolean isOptional) {
    this.serializer = serializer;
    this.config = config;
    this.isOptional = isOptional;
  }

  @Override
  public Either<ConfigError, ConfigNode> serialize(T value) {
    if (value != null) {
      return serializer.serialize(value);
    }

    if (isOptional) {
      return Either.right(ValueNode.ofNull());
    } else {
      return Either.left(new ConfigError.ExpectedNonNull());
    }
  }
}
