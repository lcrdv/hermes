package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class IntegerSerializer implements Serializer<Integer> {
  public static final SerializerFactory<Integer> FACTORY = SerializerFactory.staticFactory(
      type -> type.clazz() == int.class || type.clazz() == Integer.class,
      new IntegerSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Integer value) {
    return Either.right(ValueNode.of(value));
  }
}
