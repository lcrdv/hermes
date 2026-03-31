package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ListNode;
import dev.lacrid.hermes.type.ValueType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ListSerializerFactory implements SerializerFactory<List> {
  @Override
  public Either<ConfigError, Serializer<List>> make(ValueType<List> type, SerializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <E> Either<ConfigError, Serializer<List<E>>> internalMake(ValueType<List<E>> type, SerializerContext context) {
    Optional<ValueType<E>> elementType = type.parameterType(0);
    if (elementType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return context.serializers().find(elementType.get()).map(ListSerializer::new);
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return List.class.isAssignableFrom(type.clazz());
  }

  static class ListSerializer<E> implements Serializer<List<E>> {
    private final Serializer<E> elementSerializer;

    ListSerializer(Serializer<E> elementSerializer) {
      this.elementSerializer = elementSerializer;
    }

    @Override
    public Either<ConfigError, ConfigNode> serialize(List<E> value) {
      return Either.traverse(value, elementSerializer::serialize)
          .biMap(
              ConfigError.CollectionSerializeErrors::new,
              ListNode::new
          );
    }
  }
}
