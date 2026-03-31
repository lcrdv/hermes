package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class ShortDeserializer extends ValueNodeDeserializer<Short> {
  public static final DeserializerFactory<Short> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == short.class || type.clazz() == Short.class,
      (type, context) -> new ShortDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public ShortDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Short> deserializeNode(ValueNode node, Short defaultValue) {
    return switch (node.holder()) {
      case ValueHolder.ShortHolder(short value) -> Either.right(value);
      default -> {
        String value = node.readString();
        try {
          yield Either.right(Short.parseShort(value));
        } catch (NumberFormatException e) {
          yield Either.left(errors.generic(node));
        }
      }
    };
  }
}
