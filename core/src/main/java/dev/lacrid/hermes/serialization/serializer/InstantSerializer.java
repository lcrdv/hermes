package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.time.Instant;

public final class InstantSerializer implements Serializer<Instant> {
  public static final SerializerFactory<Instant> FACTORY = SerializerFactory.staticFactory(
      Instant.class,
      new InstantSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Instant value) {
    return Either.right(ValueNode.of(value.getEpochSecond()));
  }
}
