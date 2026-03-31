package dev.lacrid.hermes.serialization.serializer.provider;

import dev.lacrid.hermes.annotations.OptionalValue;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.NullSerializer;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.hermes.serialization.serializer.SerializerContext;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public class NullSerializerProvider implements SerializerProvider {
  private final SerializerProvider delegate;

  public NullSerializerProvider(SerializerProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> Either<ConfigError, Serializer<T>> serializer(ValueType<T> type, SerializerContext context) {
    return delegate.serializer(type, context)
        .map(serializer -> new NullSerializer<>(serializer, context.config(), type.annotations().has(OptionalValue.class)));
  }
}
