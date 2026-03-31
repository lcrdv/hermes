package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class FloatDeserializer extends ValueNodeDeserializer<Float> {
  public static final DeserializerFactory<Float> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == float.class || type.clazz() == Float.class,
      (type, context) -> new FloatDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public FloatDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Float> deserializeNode(ValueNode node, Float defaultValue) {
    return switch (node.holder()) {
      case ValueHolder.IntegerHolder(int value) -> Either.right((float) value);
      case ValueHolder.ShortHolder(short value) -> Either.right((float) value);
      default -> {
        String value = node.readString();
        try {
          yield Either.right(Float.parseFloat(value));
        } catch (NumberFormatException e) {
          yield Either.left(errors.generic(node));
        }
      }
    };
  }
}
