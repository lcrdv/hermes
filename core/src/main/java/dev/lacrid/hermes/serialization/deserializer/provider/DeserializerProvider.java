package dev.lacrid.hermes.serialization.deserializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.deserializer.DeserializerContext;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public interface DeserializerProvider {
  <T> Either<ConfigError, Deserializer<T>> deserializer(ValueType<T> type, DeserializerContext context);
}
