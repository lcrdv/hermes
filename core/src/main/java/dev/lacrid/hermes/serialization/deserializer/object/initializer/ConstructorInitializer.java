package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.annotations.Named;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConstructorInitializer<T> implements ObjectInitializer<T> {
  private final MethodHandle constructor;

  ConstructorInitializer(MethodHandle constructor) {
    this.constructor = constructor;
  }

  @Override
  public Either<ConfigError, T> initialize(List<Object> values) {
    try {
      return Either.right((T) constructor.invokeWithArguments(values));
    } catch (Throwable e) {
      return Either.left(new ConfigError.ObjectInitializationError(e.getMessage()));
    }
  }

  public static <T> Either<ConfigError, InitializerSpec<T>> from(Constructor<T> constructor) {
    if (Modifier.isPrivate(constructor.getModifiers())) {
      return Either.left(new ConfigError.InaccessibleConstructor("private constructor"));
    }

    if (constructor.getParameterCount() == 0) {
      return Either.left(new ConfigError.ConstructorMissingParameters());
    }

    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodHandle constructorHandle;

    try {
      constructorHandle = lookup.unreflectConstructor(constructor);
    } catch (IllegalAccessException e) {
      return Either.left(new ConfigError.InaccessibleConstructor(e.getMessage()));
    }

    return Arrays.stream(constructor.getParameters())
        .map(ConstructorInitializer::resolveParameter)
        .collect(Either.collector())
        .<ConfigError>mapLeft(ConfigError.GroupedErrors::new)
        .map(arguments -> new InitializerSpec<>(new ConstructorInitializer<>(constructorHandle), arguments));
  }

  private static Either<ConfigError, InitializerArgument> resolveParameter(Parameter parameter) {
    ValueType<?> parameterType = ValueType.from(parameter);
    Optional<String> parameterName = parameterType.annotations()
        .find(Named.class)
        .map(Named::value)
        .or(() -> parameter.isNamePresent() ? Optional.of(parameter.getName()) : Optional.empty());

    if (parameterName.isEmpty()) {
      return Either.left(new ConfigError.UnnamedConstructorParameter(parameterType));
    }

    return Either.right(new InitializerArgument(parameterName.get(), parameterType));
  }
}
