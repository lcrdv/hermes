package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.annotations.Config;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.ObjectPropertyFactory;
import dev.lacrid.hermes.serialization.serializer.object.ObjectSerializerFactory;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.util.FieldScanner;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ConfigSerializerFactory implements SerializerFactory<Object> {
  @Override
  public Either<ConfigError, Serializer<Object>> make(ValueType<Object> type, SerializerContext context) {
    return internal(type, context);
  }

  private <T> Either<ConfigError, Serializer<T>> internal(ValueType<T> type, SerializerContext context) {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    FieldScanner fieldScanner = new FieldScanner(type.clazz());
    List<Field> fields = fieldScanner.findFields(FieldScanner.INSTANCE);
    ObjectPropertyFactory propertyFactory = new ObjectPropertyFactory(lookup);
    ObjectSerializerFactory objectSerializerFactory = new ObjectSerializerFactory(context);

    return Either.traverse(fields, propertyFactory::<T>from)
        .flatMap(properties -> objectSerializerFactory.create(properties, type))
        .mapLeft(errors -> new ConfigError.TypedSerializeErrors(type.clazz(), errors));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.annotations().find(Config.class).isPresent()
        || Arrays.stream(type.clazz().getDeclaredConstructors()).anyMatch(constructor -> constructor.isAnnotationPresent(Config.class));
  }
}
