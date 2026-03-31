package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class LongDeserializer extends ValueNodeDeserializer<Long> {
  public static final DeserializerFactory<Long> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == long.class || type.clazz() == Long.class,
      (type, context) -> new LongDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public LongDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Long> deserializeNode(ValueNode node, Long defaultValue) {
    return switch (node.holder()) {
      case ValueHolder.IntegerHolder(int value) -> Either.right((long) value);
      case ValueHolder.ShortHolder(short value) -> Either.right((long) value);
      case ValueHolder.LongHolder(long value) -> Either.right(value);
      default -> {
        String value = node.readString();
        try {
          yield Either.right(Long.parseLong(value));
        } catch (NumberFormatException e) {
          yield Either.left(errors.generic(node));
        }
      }
    };
  }
}
