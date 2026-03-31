package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class IntegerDeserializer extends ValueNodeDeserializer<Integer> {
  public static final DeserializerFactory<Integer> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == int.class || type.clazz() == Integer.class,
      (type, context) -> new IntegerDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public IntegerDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Integer> deserializeNode(ValueNode node, Integer defaultValue) {
    return switch (node.holder()) {
      case ValueHolder.IntegerHolder(int value) -> Either.right(value);
      case ValueHolder.ShortHolder(short value) -> Either.right((int) value);
      case ValueHolder.LongHolder(long value) -> value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE
          ? Either.right((int) value)
          : Either.left(errors.generic(node, "number out of range"));
      default -> {
        String value = node.readString();
        try {
          yield Either.right(Integer.parseInt(value));
        } catch (NumberFormatException e) {
          yield Either.left(errors.generic(node));
        }
      }
    };
  }
}
