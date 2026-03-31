package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class LongSerializer implements Serializer<Long> {
  public static final SerializerFactory<Long> FACTORY = SerializerFactory.staticFactory(
      type -> type.clazz() == long.class || type.clazz() == Long.class,
      new LongSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Long value) {
    return Either.right(ValueNode.of(value));
  }
}
