package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class DoubleDeserializer extends ValueNodeDeserializer<Double> {
  public static final DeserializerFactory<Double> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == double.class || type.clazz() == Double.class,
      (type, context) -> new DoubleDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public DoubleDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Double> deserializeNode(ValueNode node, Double defaultValue) {
    return switch (node.holder()) {
      case ValueHolder.IntegerHolder(int value) -> Either.right((double) value);
      case ValueHolder.ShortHolder(short value) -> Either.right((double) value);
      default -> {
        String value = node.readString();
        try {
          yield Either.right(Double.parseDouble(value));
        } catch (NumberFormatException e) {
          yield Either.left(errors.generic(node));
        }
      }
    };
  }
}
