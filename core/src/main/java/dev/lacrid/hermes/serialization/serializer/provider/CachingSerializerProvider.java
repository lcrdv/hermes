package dev.lacrid.hermes.serialization.serializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.hermes.serialization.serializer.SerializerContext;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.HashMap;
import java.util.Map;

public final class CachingSerializerProvider implements SerializerProvider {
  private final SerializerProvider delegate;
  private final Map<ValueType, Serializer> serializerCache = new HashMap<>();

  public CachingSerializerProvider(SerializerProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> Either<ConfigError, Serializer<T>> serializer(ValueType<T> type, SerializerContext context) {
    Serializer<T> cachedSerializer = serializerCache.get(type);
    if (cachedSerializer != null) {
      return Either.right(cachedSerializer);
    }

    return delegate.serializer(type, context).peek(serializer -> serializerCache.put(type, serializer));
  }
}
