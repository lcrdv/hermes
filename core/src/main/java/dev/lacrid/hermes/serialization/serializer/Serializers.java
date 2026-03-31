package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.serialization.serializer.provider.SerializerProvider;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public final class Serializers {
  private final SerializerProvider provider;
  private final SerializerContext context;

  public Serializers(SerializerProvider provider, SerializationConfig config) {
    this.provider = provider;
    this.context = new SerializerContext(this, config);
  }

  public Serializers(SerializerProvider provider) {
    this(provider, new SerializationConfig());
  }

  public <T> Either<ConfigError, Serializer<T>> find(ValueType<T> type) {
    return provider.serializer(type, context);
  }

  public <T> Either<ConfigError, ConfigNode> serialize(T value, ValueType<T> type) {
    return find(type).flatMap(serializer -> serializer.serialize(value));
  }
}
