package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.time.Instant;

// TODO: human-readable format
public final class InstantDeserializer extends ValueNodeDeserializer<Instant> {
  public static final DeserializerFactory<Instant> FACTORY = DeserializerFactory.forType(
      type -> type.clazz() == Instant.class,
      (type, context) -> new InstantDeserializer(context.errorFactory(type))
  );

  private final NodeErrors errors;

  public InstantDeserializer(NodeErrors errors) {
    this.errors = errors;
  }

  @Override
  protected Either<ConfigError, Instant> deserializeNode(ValueNode node, Instant defaultValue) {
    try {
      return Either.right(Instant.ofEpochMilli(Long.parseLong(node.readString())));
    } catch (IllegalArgumentException e) {
      return Either.left(errors.generic(node));
    }
  }
}
