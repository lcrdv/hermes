package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.math.BigDecimal;

public class BigDecimalSerializer implements Serializer<BigDecimal> {
  public static final SerializerFactory<BigDecimal> FACTORY = SerializerFactory.staticFactory(
      BigDecimal.class,
      new BigDecimalSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(BigDecimal value) {
    return Either.right(ValueNode.of(value.toString()));
  }
}
