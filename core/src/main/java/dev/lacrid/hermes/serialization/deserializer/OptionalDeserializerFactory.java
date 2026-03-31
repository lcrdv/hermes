package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public final class OptionalDeserializerFactory implements DeserializerFactory<Optional<?>> {
  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Either<ConfigError, Deserializer<Optional<?>>> make(ValueType<Optional<?>> type, DeserializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <T> Either<ConfigError, Deserializer<Optional<T>>> internalMake(ValueType<Optional<T>> type, DeserializerContext context) {
    Optional<ValueType<T>> parameterType = type.parameterType(0);
    if (parameterType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    SerializationConfig config = context.config();
    return context.deserializers().find(parameterType.get())
        .map(parameterDeserializer -> new OptionalDeserializer<>(parameterDeserializer, config));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.clazz() == Optional.class;
  }

  private static class OptionalDeserializer<T> implements Deserializer<Optional<T>> {
    private final Deserializer<T> valueDeserializer;
    private final SerializationConfig config;

    OptionalDeserializer(Deserializer<T> valueDeserializer, SerializationConfig config) {
      this.valueDeserializer = valueDeserializer;
      this.config = config;
    }

    @Override
    public Either<ConfigError, Optional<T>> deserialize(ConfigNode node, Optional<T> defaultValue) {
      if (node instanceof NullNode) {
        return Either.right(defaultValue != null && config.explicitOptionals() ? defaultValue : Optional.empty());
      }

      if ((config.treatNullAsEmpty() || config.explicitOptionals())
          && node instanceof ValueNode valueNode
          && valueNode.holder() instanceof ValueHolder.NullHolder) {
        return Either.right(Optional.empty());
      }

      // is null allowed inside?
      return valueDeserializer.deserialize(node, defaultValue.orElse(null))
          .map(Optional::ofNullable);
    }
  }
}
