package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandle;
import java.util.List;

public class StaticFieldSetter<T> implements ObjectInitializer<T> {
  private final List<MethodHandle> setters;

  StaticFieldSetter(List<MethodHandle> setters) {
    this.setters = setters;
  }

  @Override
  public Either<ConfigError, T> initialize(List<Object> values) {
    try {
      for (int i = 0; i < setters.size(); i++) {
        setters.get(i).invoke(values.get(i));
      }

      return Either.right(null);
    } catch (Throwable e) {
      return Either.left(new ConfigError.ObjectInitializationError(e.getMessage()));
    }
  }
}
