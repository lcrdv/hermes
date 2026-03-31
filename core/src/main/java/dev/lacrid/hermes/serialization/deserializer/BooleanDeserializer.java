package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

public final class BooleanDeserializer extends ValueNodeDeserializer<Boolean> {
  public static final DeserializerFactory<Boolean> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == boolean.class || type.clazz() == Boolean.class,
      (type, context) -> new BooleanDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public BooleanDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Boolean> deserializeNode(ValueNode node, Boolean defaultValue) {
    if (node.holder() instanceof ValueHolder.BooleanHolder(boolean value)) {
      return Either.right(value);
    }

    return switch (node.readString().toLowerCase()) {
      case "true", "t", "1", "yes" -> Either.right(true);
      case "false", "f", "0", "no" -> Either.right(false);
      default -> Either.left(errors.generic(node));
    };
  }
}
