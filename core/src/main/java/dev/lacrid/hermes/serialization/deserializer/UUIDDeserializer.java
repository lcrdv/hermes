package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.util.UUID;

public final class UUIDDeserializer extends ValueNodeDeserializer<UUID> {
  public static final DeserializerFactory<UUID> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == UUID.class,
      (type, context) -> new UUIDDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public UUIDDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, UUID> deserializeNode(ValueNode node, UUID defaultValue) {
    try {
      return Either.right(UUID.fromString(node.readString()));
    } catch (IllegalArgumentException e) {
      return Either.left(errors.generic(node));
    }
  }
}
