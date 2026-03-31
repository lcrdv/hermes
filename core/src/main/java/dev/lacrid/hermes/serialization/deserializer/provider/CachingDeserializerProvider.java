package dev.lacrid.hermes.serialization.deserializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.deserializer.DeserializerContext;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.HashMap;
import java.util.Map;

public class CachingDeserializerProvider implements DeserializerProvider {
  private final DeserializerProvider delegate;
  private final Map<ValueType, Deserializer> deserializerCache = new HashMap<>();

  public CachingDeserializerProvider(DeserializerProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> Either<ConfigError, Deserializer<T>> deserializer(ValueType<T> type, DeserializerContext context) {
    Deserializer<T> cachedDeserializer = deserializerCache.get(type);
    if (cachedDeserializer != null) {
      return Either.right(cachedDeserializer);
    }

    return delegate.deserializer(type, context).peek(deserializer -> deserializerCache.put(type, deserializer));
  }
}
