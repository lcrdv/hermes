package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Map;
import java.util.Optional;

public final class MapSerializerFactory implements SerializerFactory<Map> {
  @Override
  public Either<ConfigError, Serializer<Map>> make(ValueType<Map> type, SerializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <K, V> Either<ConfigError, Serializer<Map<K, V>>> internalMake(ValueType<Map<K, V>> type, SerializerContext context) {
    Optional<ValueType<K>> keyType = type.parameterType(0);
    Optional<ValueType<V>> valueType = type.parameterType(1);
    if (keyType.isEmpty() || valueType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return Either.combine(
        context.serializers().find(keyType.get()),
        context.serializers().find(valueType.get()),
        MapSerializer::new,
        (keyError, valueError) -> null
    );
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return Map.class.isAssignableFrom(type.clazz());
  }

  static class MapSerializer<K, V> implements Serializer<Map<K, V>> {
    private final Serializer<K> keySerializer;
    private final Serializer<V> valueSerializer;

    MapSerializer(Serializer<K> keySerializer, Serializer<V> valueSerializer) {
      this.keySerializer = keySerializer;
      this.valueSerializer = valueSerializer;
    }

    @Override
    public Either<ConfigError, ConfigNode> serialize(Map<K, V> value) {
      return Either.traverse(value.entrySet(), entry -> Either.combine(
          keySerializer.serialize(entry.getKey())
              .flatMap(node -> node instanceof ValueNode valueNode
                  ? Either.right(NodeKey.of(valueNode.readString()))
                  : Either.left(new ConfigError.UnexpectedKeyNode(node.getClass()))),
          valueSerializer.serialize(entry.getValue()),
          MapNode.Entry::new,
          (keyError, nodeError) -> null
      )).biMap(
          ConfigError.CollectionSerializeErrors::new,
          MapNode::new
      );
    }
  }
}
