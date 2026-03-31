package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public final class OptionalSerializerFactory implements SerializerFactory<Optional<Object>> {
  @Override
  public Either<ConfigError, Serializer<Optional<Object>>> make(ValueType<Optional<Object>> type, SerializerContext context) {
    Optional<ValueType<Object>> parameterType = type.parameterType(0);
    if (parameterType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return context.serializers().find(parameterType.get())
        .map(parameterSerializer -> new OptionalSerializer<>(parameterSerializer, false));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.clazz() == Optional.class;
  }

  private static class OptionalSerializer<T> implements Serializer<Optional<T>> {
    private final Serializer<T> valueSerializer;
    private final boolean explicitOptionals;

    private OptionalSerializer(Serializer<T> valueSerializer, boolean explicitOptionals) {
      this.valueSerializer = valueSerializer;
      this.explicitOptionals = explicitOptionals;
    }

    @Override
    public Either<ConfigError, ConfigNode> serialize(Optional<T> value) {
      if (value.isEmpty()) {
        return Either.right(explicitOptionals ? ValueNode.ofNull() : NullNode.create());
      }

      return valueSerializer.serialize(value.get());
    }
  }
}
