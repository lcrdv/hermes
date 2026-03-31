package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.util.Priority;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.ObjectPropertyFactory;
import dev.lacrid.hermes.serialization.serializer.object.ObjectSerializerFactory;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.util.MethodScanner;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;

public final class ProxySerializerFactory implements SerializerFactory<Object> {
  @Override
  public Either<ConfigError, Serializer<Object>> make(ValueType<Object> type, SerializerContext context) {
    return internal(type, context);
  }

  private <T> Either<ConfigError, Serializer<T>> internal(ValueType<T> type, SerializerContext context) {
    Class<T> clazz = type.clazz();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodScanner scanner = new MethodScanner(clazz);
    List<Method> methods = scanner.findMethods(MethodScanner.GETTERS);
    ObjectPropertyFactory propertyFactory = new ObjectPropertyFactory(lookup);
    ObjectSerializerFactory objectSerializerFactory = new ObjectSerializerFactory(context);

    return Either.<ConfigError, ObjectProperty<?, T>, Method>traverse(methods, propertyFactory::from)
        .flatMap(properties -> objectSerializerFactory.create(properties, type))
        .mapLeft(errors -> new ConfigError.TypedSerializeErrors(clazz, errors));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.isInterface();
  }

  @Override
  public Priority priority() {
    return Priority.LOW;
  }
}
