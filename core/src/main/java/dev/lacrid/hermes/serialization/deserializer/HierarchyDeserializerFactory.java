package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.naming.NameResolver;
import dev.lacrid.hermes.naming.NamingStrategies;
import dev.lacrid.hermes.naming.RegexLexer;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.serialization.TypeHierarchy;
import dev.lacrid.hermes.serialization.TypeName;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class HierarchyDeserializerFactory implements DeserializerFactory<Object> {
  @Override
  public Either<ConfigError, Deserializer<Object>> make(ValueType<Object> type, DeserializerContext context) {
    return internal(type, context);
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return ((type.clazz().isInterface() || Modifier.isAbstract(type.clazz().getModifiers())) && type.annotations().has(TypeName.class))
        || type.clazz().isSealed();
  }

  private <T> Either<ConfigError, Deserializer<T>> internal(ValueType<T> type, DeserializerContext context) {
    return TypeHierarchy.from(type, new NameResolver(new RegexLexer(), NamingStrategies.SNAKE_CASE))
        .map(hierarchy -> new HierarchyDeserializer<>(type, hierarchy, context.deserializers()));
  }

  private static class HierarchyDeserializer<T> implements Deserializer<T> {
    private final ValueType<T> type;
    private final TypeHierarchy typeHierarchy;
    private final Deserializers deserializers;
    private final Map<Class<?>, Either<ConfigError, Deserializer<?>>> resolvedDeserializers = new ConcurrentHashMap<>();

    private HierarchyDeserializer(ValueType<T> type, TypeHierarchy typeHierarchy, Deserializers deserializers) {
      this.type = type;
      this.typeHierarchy = typeHierarchy;
      this.deserializers = deserializers;
    }

    @Override
    public Either<ConfigError, T> deserialize(ConfigNode node, T defaultValue) {
      if (!(node instanceof MapNode mapNode)) {
        return Either.left(ConfigError.unexpectedNode(MapNode.class, node));
      }

      Optional<ValueNode> typeNode = mapNode.findByKey(NodeKey.of("type"))
          .filter(n -> n instanceof ValueNode)
          .map(n -> (ValueNode) n);
      if (typeNode.isEmpty()) {
        return Either.left(new ConfigError.UnknownHierarchyType());
      }

      String typeName = typeNode.get().readString();
      Optional<Class<?>> actualTypeToken = typeHierarchy.type(typeName);
      if (actualTypeToken.isEmpty()) {
        return Either.left(new ConfigError.UnknownType(typeName));
      }

      return resolvedDeserializers.computeIfAbsent(actualTypeToken.get(),
              k -> Either.narrow(deserializers.find(type.toSubType(actualTypeToken.get()))))
          .flatMap(deserializer -> ((Deserializer<T>) deserializer).deserialize(node, defaultValue));
    }
  }
}
