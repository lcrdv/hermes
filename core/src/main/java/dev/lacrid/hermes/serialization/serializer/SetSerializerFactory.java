package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ListNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Set;
import java.util.Optional;

public final class SetSerializerFactory implements SerializerFactory<Set> {
  @Override
  public Either<ConfigError, Serializer<Set>> make(ValueType<Set> type, SerializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <E> Either<ConfigError, Serializer<Set<E>>> internalMake(ValueType<Set<E>> type, SerializerContext context) {
    Optional<ValueType<E>> elementType = type.parameterType(0);
    if (elementType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return context.serializers().find(elementType.get()).map(SetSerializer::new);
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return Set.class.isAssignableFrom(type.clazz());
  }

  static class SetSerializer<E> implements Serializer<Set<E>> {
    private final Serializer<E> elementSerializer;

    SetSerializer(Serializer<E> elementSerializer) {
      this.elementSerializer = elementSerializer;
    }

    @Override
    public Either<ConfigError, ConfigNode> serialize(Set<E> value) {
      return Either.traverse(value, elementSerializer::serialize)
          .biMap(
              ConfigError.CollectionSerializeErrors::new,
              ListNode::new
          );
    }
  }
}
