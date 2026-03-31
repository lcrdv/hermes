package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public class DoubleSerializer implements Serializer<Double> {
  public static final SerializerFactory<Double> FACTORY = SerializerFactory.staticFactory(
      type -> type.clazz() == double.class || type.clazz() == Double.class,
      new DoubleSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(Double value) {
    return Either.right(ValueNode.of(value));
  }
}
