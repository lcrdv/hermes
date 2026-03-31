package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.util.UUID;

public final class UUIDSerializer implements Serializer<UUID> {
  public static final SerializerFactory<UUID> FACTORY = SerializerFactory.staticFactory(UUID.class, new UUIDSerializer());

  @Override
  public Either<ConfigError, ConfigNode> serialize(UUID value) {
    return Either.right(ValueNode.of(value.toString()));
  }
}
