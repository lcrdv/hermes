package dev.lacrid.hermes.serialization.serializer.object;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.TypedPathResolver;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.hermes.serialization.serializer.SerializerContext;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;

public class ObjectSerializerFactory {
  private final SerializerContext context;

  public ObjectSerializerFactory(SerializerContext context) {
    this.context = context;
  }

  public <T> Either<List<ConfigError>, Serializer<T>> create(List<ObjectProperty<?, T>> properties, ValueType<T> type) {
    TypedPathResolver typedPathResolver = context.config().pathResolver(type);
    return Either.<ConfigError, ObjectPropertySerializer<?, T>, ObjectProperty<?, T>>traverse(properties, property -> createPropertySerializer(property, typedPathResolver))
        .map(serializers -> new ObjectSerializer<>(serializers, type.clazz()));
  }

  private <P, T> Either<ConfigError, ObjectPropertySerializer<P, T>> createPropertySerializer(ObjectProperty<P, T> property, TypedPathResolver typedPathResolver) {
    NodePath path = typedPathResolver.resolve(property.name(), property.type());
    return context.serializers().find(property.type()).biMap(
        error -> new ConfigError.KeyedError(property.name(), error),
        deserializer -> new ObjectPropertySerializer<>(deserializer, property.accessor(), path)
    );
  }
}
