package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public final class Processors {
  public static <F, T, R> List<R> create(List<F> factories, BiFunction<F, ValueType<T>, Optional<R>> factoryFunction, ValueType<T> type) {
    return factories.stream()
        .map(factory -> factoryFunction.apply(factory, type))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public static <P, V> Either<ConfigError, V> process(List<P> processors, BiFunction<P, V, Either<ConfigError, V>> process, V value) {
    V currentValue = value;
    for (P processor : processors) {
      var result = process.apply(processor, currentValue);
      if (result.isLeft()) {
        return result;
      }
      currentValue = result.getRight();
    }

    return Either.right(currentValue);
  }
}
