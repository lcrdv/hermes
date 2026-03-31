package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.util.Priority;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface DeserializerFactory<T> {
  Either<ConfigError, Deserializer<T>> make(ValueType<T> type, DeserializerContext context);

  boolean supports(ValueType<?> type);

  default Priority priority() {
    return Priority.NORMAL;
  }

  static <T> DeserializerFactory<T> staticFactory(Class<T> requiredType, Deserializer<T> deserializer) {
    return forType(requiredType, (type, context) -> deserializer);
  }

  static <T> DeserializerFactory<T> forType(Class<T> requiredType, BiFunction<ValueType<T>, DeserializerContext, Deserializer<T>> provider) {
    return forType(type -> type.clazz().equals(requiredType), provider);
  }

  static <T> DeserializerFactory<T> forType(Predicate<ValueType<?>> check, BiFunction<ValueType<T>, DeserializerContext, Deserializer<T>> provider) {
    return new DeserializerFactory<>() {
      @Override
      public Either<ConfigError, Deserializer<T>> make(ValueType<T> type, DeserializerContext context) {
        return Either.right(provider.apply(type, context));
      }

      @Override
      public boolean supports(ValueType<?> type) {
        return check.test(type);
      }
    };
  }
}
