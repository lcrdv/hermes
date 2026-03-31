package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.serialization.deserializer.supplier.TypeSupplierFactory;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;
import dev.lacrid.lambda.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapDeserializerFactory implements DeserializerFactory<Map> {
  private final TypeSupplierFactory<Map> mapSupplier;

  {
    mapSupplier = new TypeSupplierFactory<>(() -> new LinkedHashMap<>());
    mapSupplier.registerSupplier(Map.class, LinkedHashMap::new);
    mapSupplier.registerSupplier(HashMap.class, HashMap::new);
    mapSupplier.registerSupplier(ConcurrentHashMap.class, ConcurrentHashMap::new);
    mapSupplier.registerTypeSupplier(EnumMap.class, type -> new EnumMap(type.parameterType(0).map(ValueType::clazz).orElseThrow()));
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Either<ConfigError, Deserializer<Map>> make(ValueType<Map> type, DeserializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <KeyT, ValueT> Either<ConfigError, Deserializer<Map<KeyT, ValueT>>> internalMake(ValueType<Map<KeyT, ValueT>> type, DeserializerContext context) {
    Optional<ValueType<KeyT>> keyType = type.parameterType(0);
    Optional<ValueType<ValueT>> valueType = type.parameterType(1);
    if (keyType.isEmpty() || valueType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return Either.combine(
        context.deserializers().find(keyType.get()),
        context.deserializers().find(valueType.get()),
        (keyDeserializer, valueDeserializer) -> new MapDeserializer<>(keyDeserializer, valueDeserializer, mapSupplier.supplierFor(type)),
        (keyError, valueError) -> null
    );
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return Map.class.isAssignableFrom(type.clazz());
  }

  static class MapDeserializer<KeyT, ValueT> extends SpecificNodeDeserializer<MapNode, Map<KeyT, ValueT>> {
    private final Deserializer<KeyT> keyDeserializer;
    private final Deserializer<ValueT> elementDeserializer;
    private final Supplier<Map<KeyT, ValueT>> mapFactory;

    MapDeserializer(Deserializer<KeyT> keyDeserializer, Deserializer<ValueT> elementDeserializer, Supplier<Map<KeyT, ValueT>> mapFactory) {
      this.keyDeserializer = keyDeserializer;
      this.elementDeserializer = elementDeserializer;
      this.mapFactory = mapFactory;
    }

    @Override
    protected Either<ConfigError, Map<KeyT, ValueT>> deserializeNode(MapNode node, Map<KeyT, ValueT> defaultValue) {
      return Either.traverse(node.entries(), entry ->
          Either.combine(
              keyDeserializer.deserialize(entry.key().asNode(), null),
              elementDeserializer.deserialize(entry.node(), null),
              Pair::new,
              (keyError, nodeError) -> null
          )
      ).biMap(
          errors -> new ConfigError.TypedDeserializeErrors(Map.class, errors),
          values -> values.stream()
              .collect(Collectors.toMap(Pair::first, Pair::second, (o1, o2) -> o1, mapFactory))
      );
    }
  }
}
