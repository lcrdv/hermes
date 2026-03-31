package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.serialization.deserializer.provider.DeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public final class Deserializers {
  private final DeserializerProvider provider;
  private final DeserializerContext context;

  public Deserializers(DeserializerProvider provider, SerializationConfig config) {
    this.provider = provider;
    this.context = new DeserializerContext(this, config);
  }

  public Deserializers(DeserializerProvider provider) {
    this(provider, new SerializationConfig());
  }

  public <T> Either<ConfigError, Deserializer<T>> find(ValueType<T> type) {
    return provider.deserializer(type, context);
  }

  public <T> Either<ConfigError, T> deserialize(ConfigNode value, ValueType<T> type) {
    return deserialize(value, type, null);
  }

  public <T> Either<ConfigError, T> deserialize(ConfigNode value, ValueType<T> type, T defaultValue) {
    return find(type).flatMap(deserializer -> deserializer.deserialize(value, defaultValue));
  }
}
