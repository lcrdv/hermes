package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.math.BigDecimal;

public final class BigDecimalDeserializer extends ValueNodeDeserializer<BigDecimal> {
  public static final DeserializerFactory<BigDecimal> FACTORY = DeserializerFactory.forType(
      BigDecimal.class,
      (type, context) -> new BigDecimalDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public BigDecimalDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, BigDecimal> deserializeNode(ValueNode node, BigDecimal defaultValue) {
    try {
      return Either.right(new BigDecimal(node.readString()));
    } catch (NumberFormatException e) {
      return Either.left(errors.generic(node));
    }
  }
}
