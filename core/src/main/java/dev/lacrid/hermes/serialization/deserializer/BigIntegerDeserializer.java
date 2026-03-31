package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.math.BigInteger;

public final class BigIntegerDeserializer extends ValueNodeDeserializer<BigInteger> {
  public static final DeserializerFactory<BigInteger> FACTORY = DeserializerFactory.forType(
      BigInteger.class,
      (type, context) -> new BigIntegerDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public BigIntegerDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, BigInteger> deserializeNode(ValueNode node, BigInteger defaultValue) {
    try {
      return Either.right(new BigInteger(node.readString()));
    } catch (NumberFormatException e) {
      return Either.left(errors.generic(node));
    }
  }
}
