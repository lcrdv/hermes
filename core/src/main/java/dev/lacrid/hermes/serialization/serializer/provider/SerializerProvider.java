package dev.lacrid.hermes.serialization.serializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.SerializerContext;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.hermes.serialization.serializer.SerializerFactory;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;

public interface SerializerProvider {
  <T> Either<ConfigError, Serializer<T>> serializer(ValueType<T> type, SerializerContext context);
}
