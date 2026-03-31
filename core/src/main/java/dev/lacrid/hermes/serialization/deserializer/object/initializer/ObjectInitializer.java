package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.lambda.Either;

import java.util.List;

public interface ObjectInitializer<T> {
  Either<ConfigError, T> initialize(List<Object> values);
}
