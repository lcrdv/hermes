package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.annotations.Config;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.ObjectPropertyFactory;
import dev.lacrid.hermes.serialization.deserializer.object.DefaultValue;
import dev.lacrid.hermes.serialization.deserializer.object.ObjectDeserializerFactory;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.ConstructorInitializer;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InitializerSpec;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InstanceFieldSetter;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.util.FieldScanner;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static dev.lacrid.lambda.Context.either;

public class ConfigDeserializerFactory implements DeserializerFactory<Object> {
  @Override
  public Either<ConfigError, Deserializer<Object>> make(ValueType<Object> type, DeserializerContext context) {
    Class<Object> clazz = type.clazz();

    FieldScanner fieldScanner = new FieldScanner(clazz);
    List<Field> instanceFields = fieldScanner.findFields(FieldScanner.INSTANCE);
    List<Field> staticFields = fieldScanner.findFields(FieldScanner.STATIC);

    boolean isStatic = !staticFields.isEmpty() && instanceFields.isEmpty();
    var result = isStatic ? createStaticConfig(staticFields, type, context) : createInstanceConfig(instanceFields, type, context);

    return result.mapLeft(errors -> new ConfigError.TypedDeserializeErrors(clazz, errors));
  }

  private static <T> Either<List<ConfigError>, Deserializer<T>> createStaticConfig(List<Field> fields, ValueType<T> type, DeserializerContext context) {
    return Either.left(null);
  }

  private static <T> Either<List<ConfigError>, Deserializer<T>> createInstanceConfig(List<Field> fields, ValueType<T> type, DeserializerContext context) {
    Class<T> clazz = type.clazz();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    ObjectDeserializerFactory<T> deserializerFactory = new ObjectDeserializerFactory<>(context);

    return either(ctx -> {
      List<ObjectProperty<?, T>> properties = ctx.bind(createProperties(lookup, fields));
      DefaultValue<T> defaultValue = defaultValue(clazz);
      List<InitializerSpec<T>> initializers = ctx.bind(constructors(clazz));
      var fieldSetter = instanceFieldSetter(clazz, fields);

      if (fieldSetter.isLeft() && initializers.isEmpty()) {
        ctx.raise(Collections.singletonList(fieldSetter.getLeft()));
      }

      fieldSetter.handleRight(initializers::add);

      return ctx.bind(deserializerFactory.create(properties, initializers, defaultValue, type));
    });
  }

  private static <T> Either<List<ConfigError>, List<ObjectProperty<?, T>>> createProperties(MethodHandles.Lookup lookup, List<Field> fields) {
    ObjectPropertyFactory propertyFactory = new ObjectPropertyFactory(lookup);
    return Either.traverse(fields, propertyFactory::from);
  }

  private static <T> DefaultValue<T> defaultValue(Class<T> type) {
    try {
      return DefaultValue.noArgsConstructor(type.getDeclaredConstructor());
    } catch (Exception e) {
      return DefaultValue.none();
    }
  }

  private static <T> Either<ConfigError, InitializerSpec<T>> instanceFieldSetter(Class<T> clazz, List<Field> fields) {
    try {
      return InstanceFieldSetter.from(clazz.getDeclaredConstructor(), fields);
    } catch (NoSuchMethodException e) {
      return Either.left(new ConfigError.MissingNoArgsConstructor());
    }
  }

  private static <T> Either<List<ConfigError>, List<InitializerSpec<T>>> constructors(Class<T> type) {
    return Stream.of(type.getDeclaredConstructors())
        .filter(constructor -> constructor.isAnnotationPresent(Config.class) && constructor.getParameterCount() != 0)
        .map(constructor -> ConstructorInitializer.from((Constructor<T>) constructor))
        .collect(Either.collector());
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.annotations().find(Config.class).isPresent()
        || Arrays.stream(type.clazz().getDeclaredConstructors()).anyMatch(constructor -> constructor.isAnnotationPresent(Config.class));
  }
}
