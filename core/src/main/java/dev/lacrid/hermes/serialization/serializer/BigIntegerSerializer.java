package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerSerializer implements Serializer<BigInteger> {
  public static final SerializerFactory<BigInteger> FACTORY = SerializerFactory.staticFactory(
      BigInteger.class,
      new BigIntegerSerializer()
  );

  @Override
  public Either<ConfigError, ConfigNode> serialize(BigInteger value) {
    return Either.right(ValueNode.of(value.toString()));
  }
}
