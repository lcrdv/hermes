package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.annotations.Config;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.ObjectPropertyFactory;
import dev.lacrid.hermes.serialization.deserializer.object.*;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.ConstructorInitializer;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InitializerSpec;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Context;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RecordDeserializerFactory implements DeserializerFactory<Object> {
  @Override
  public Either<ConfigError, Deserializer<Object>> make(ValueType<Object> type, DeserializerContext context) {
    return internal(type, context);
  }

  private <T> Either<ConfigError, Deserializer<T>> internal(ValueType<T> type, DeserializerContext context) {
    Class<T> clazz = type.clazz();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    ObjectPropertyFactory propertyFactory = new ObjectPropertyFactory(lookup);
    List<RecordComponent> components = Arrays.asList(type.clazz().getRecordComponents());

    return Context.<List<ConfigError>, Deserializer<T>>either(ctx -> {
      List<ObjectProperty<?, T>> properties = ctx.bind(Either.traverse(components, propertyFactory::from));
      List<InitializerSpec<T>> initializers = ctx.bind(initializers(clazz).mapLeft(Collections::singletonList));

      return ctx.bind(new ObjectDeserializerFactory<T>(context).create(properties, initializers, defaultValue(clazz), type));
    }).mapLeft(errors -> new ConfigError.TypedDeserializeErrors(clazz, errors));
  }

  private static <T> Either<ConfigError, List<InitializerSpec<T>>> initializers(Class<T> type) {
    Class<?>[] canonicalTypes = Arrays.stream(type.getRecordComponents())
        .map(RecordComponent::getType)
        .toArray(Class<?>[]::new);
    List<InitializerSpec<T>> initializers = new ArrayList<>();
    ConfigError canonicalError = null;

    for (Constructor<?> declaredConstructor : type.getDeclaredConstructors()) {
      if (declaredConstructor.getParameterCount() == 0 && canonicalTypes.length > 0) {
        continue;
      }

      boolean isCanonical = Arrays.equals(canonicalTypes, declaredConstructor.getParameterTypes());
      boolean isAnnotated = declaredConstructor.isAnnotationPresent(Config.class);

      if (!isCanonical && !isAnnotated) {
        continue;
      }

      var result = ConstructorInitializer.from((Constructor<T>) declaredConstructor);
      result.handleRight(initializers::add);
      if (isCanonical && result.isLeft()) {
        canonicalError = result.getLeft();
      }
    }

    return initializers.isEmpty()
        ? Either.left(canonicalError != null ? canonicalError : new ConfigError.NoRecordConstructor())
        : Either.right(initializers);
  }

  private static <T> DefaultValue<T> defaultValue(Class<T> type) {
    try {
      return DefaultValue.noArgsConstructor(type.getDeclaredConstructor());
    } catch (Exception e) {
      return DefaultValue.none();
    }
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.clazz().isRecord();
  }
}
