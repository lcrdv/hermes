package dev.lacrid.hermes.serialization.deserializer.provider;

import dev.lacrid.hermes.annotations.OptionalValue;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.*;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public class DefaultsDeserializerProvider implements DeserializerProvider {
  private final DeserializerProvider delegate;

  public DefaultsDeserializerProvider(DeserializerProvider delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> Either<ConfigError, Deserializer<T>> deserializer(ValueType<T> type, DeserializerContext context) {
    return delegate.deserializer(type, context)
        .map(deserializer -> type.annotations().find(OptionalValue.class)
            .map(annotation ->
                (Deserializer<T>) new OptionalValueDeserializer<>(
                    deserializer,
                    annotation.value().isEmpty() ? null : () -> ValueNode.of(annotation.value()),
                    false))
            .orElseGet(() -> new RequiredValueDeserializer<>(deserializer, context.config())));
  }
}
