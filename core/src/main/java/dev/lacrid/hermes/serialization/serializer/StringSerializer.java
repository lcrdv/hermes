package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class StringSerializer implements Serializer<String> {
  public static final SerializerFactory<String> FACTORY = SerializerFactory.staticFactory(String.class, new StringSerializer());

  @Override
  public Either<ConfigError, ConfigNode> serialize(String value) {
    return Either.right(ValueNode.of(value));
  }
}
