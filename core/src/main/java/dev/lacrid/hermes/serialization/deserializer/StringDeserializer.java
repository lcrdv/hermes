package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class StringDeserializer extends ValueNodeDeserializer<String> {
  public static final DeserializerFactory<String> FACTORY = DeserializerFactory.staticFactory(String.class, new StringDeserializer());

  @Override
  protected Either<ConfigError, String> deserializeNode(ValueNode node, String defaultValue) {
    return Either.right(node.readString());
  }
}
