package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public class FloatSerializer implements Serializer<Float> {
  public static final SerializerFactory<Float> FACTORY = SerializerFactory.staticFactory(
      type -> type.clazz() == float.class || type.clazz() == Float.class,
      new FloatSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Float value) {
    return Either.right(ValueNode.of(value));
  }
}
