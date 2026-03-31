package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.naming.NameResolver;
import dev.lacrid.hermes.naming.NamingStrategies;
import dev.lacrid.hermes.naming.RegexLexer;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.serialization.TypeHierarchy;
import dev.lacrid.hermes.serialization.TypeName;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class HierarchySerializerFactory implements SerializerFactory<Object> {
  @Override
  public Either<ConfigError, Serializer<Object>> make(ValueType<Object> type, SerializerContext context) {
    return internal(type, context);
  }

  private <T> Either<ConfigError, Serializer<T>> internal(ValueType<T> type, SerializerContext context) {
    return TypeHierarchy.from(type, new NameResolver(new RegexLexer(), NamingStrategies.SNAKE_CASE))
        .map(hierarchy -> new HierarchySerializer<>(type, hierarchy, context.serializers()));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return ((type.clazz().isInterface() || Modifier.isAbstract(type.clazz().getModifiers())) && type.clazz().isAnnotationPresent(TypeName.class))
        || type.clazz().isSealed();
  }

  private static class HierarchySerializer<T> implements Serializer<T> {
    private final ValueType<T> type;
    private final TypeHierarchy typeHierarchy;
    private final Serializers serializers;
    private final Map<String, Either<ConfigError, Serializer<?>>> resolvedSerializers = new ConcurrentHashMap<>();

    private HierarchySerializer(ValueType<T> type, TypeHierarchy typeHierarchy, Serializers serializers) {
      this.type = type;
      this.typeHierarchy = typeHierarchy;
      this.serializers = serializers;
    }

    @Override
    public Either<ConfigError, ConfigNode> serialize(T value) {
      Class<?> actualTypeToken = value.getClass();
      Optional<String> name = typeHierarchy.name(actualTypeToken);
      if (name.isEmpty()) {
        return Either.left(new ConfigError.UnknownSubclass(actualTypeToken));
      }

      return resolvedSerializers.computeIfAbsent(name.get(),
          k -> Either.narrow(serializers.find(type.toSubType(actualTypeToken))))
          .flatMap(serializer -> ((Serializer<Object>) serializer).serialize(value))
          .flatMap(node -> {
            if (node instanceof MapNode mapNode) {
              mapNode.addEntry(new MapNode.Entry(NodeKey.of("type"), ValueNode.of(name.get())));
              return Either.right(mapNode);
            }

            return Either.left(new ConfigError.IncompatibleHierarchyParentNode());
          });
    }
  }
}
