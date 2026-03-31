package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.math.BigInteger;

public class BooleanSerializer implements Serializer<Boolean> {
  public static final SerializerFactory<Boolean> FACTORY = SerializerFactory.staticFactory(
      type -> type.clazz() == boolean.class || type.clazz() == Boolean.class,
      new BooleanSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Boolean value) {
    return Either.right(ValueNode.of(value));
  }
}
