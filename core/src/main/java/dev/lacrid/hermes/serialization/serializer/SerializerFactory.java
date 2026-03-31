package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.util.Priority;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface SerializerFactory<T> {
  Either<ConfigError, Serializer<T>> make(ValueType<T> type, SerializerContext context);

  boolean supports(ValueType<?> type);

  default Priority priority() {
    return Priority.NORMAL;
  }

  static <T> SerializerFactory<T> staticFactory(Class<T> requiredType, Serializer<T> serializer) {
    return forType(requiredType, (type, context) -> serializer);
  }

  static <T> SerializerFactory<T> staticFactory(Predicate<ValueType<?>> check, Serializer<T> serializer) {
    return forType(check, (type, context) -> serializer);
  }

  static <T> SerializerFactory<T> forType(Class<T> requiredType, BiFunction<ValueType<T>, SerializerContext, Serializer<T>> provider) {
    return forType(type -> type.clazz().equals(requiredType), provider);
  }

  static <T> SerializerFactory<T> forType(Predicate<ValueType<?>> check, BiFunction<ValueType<T>, SerializerContext, Serializer<T>> provider) {
    return new SerializerFactory<>() {
      @Override
      public Either<ConfigError, Serializer<T>> make(ValueType<T> type, SerializerContext context) {
        return Either.right(provider.apply(type, context));
      }

      @Override
      public boolean supports(ValueType<?> type) {
        return check.test(type);
      }
    };
  }
}
