package dev.lacrid.hermes.serialization.deserializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.deserializer.*;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FactoryDeserializerProvider implements DeserializerProvider {
  private final List<DeserializerFactory<?>> factories;

  public FactoryDeserializerProvider(List<DeserializerFactory<?>> factories) {
    this.factories = new ArrayList<>(factories);
    this.factories.sort(Comparator.comparing(DeserializerFactory::priority));
  }

  @Override
  public <T> Either<ConfigError, Deserializer<T>> deserializer(ValueType<T> type, DeserializerContext context) {
    for (DeserializerFactory<?> factory : factories) {
      if (!factory.supports(type)) {
        continue;
      }

      return ((DeserializerFactory<T>) factory).make(type, context);
    }

    return Either.left(new ConfigError.UnknownDeserializer(type));
  }
}
