package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.util.Priority;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.ObjectPropertyFactory;
import dev.lacrid.hermes.serialization.PropertySetter;
import dev.lacrid.hermes.serialization.deserializer.object.DefaultValue;
import dev.lacrid.hermes.serialization.deserializer.object.ObjectDeserializerFactory;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InitializerSpec;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.ProxyInitializer;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.util.MethodScanner;
import dev.lacrid.lambda.Context;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class ProxyDeserializerFactory implements DeserializerFactory<Object> {
  @Override
  public Either<ConfigError, Deserializer<Object>> make(ValueType<Object> type, DeserializerContext context) {
    return internal(type, context);
  }

  private <T> Either<ConfigError, Deserializer<T>> internal(ValueType<T> type, DeserializerContext context) {
    Class<T> clazz = type.clazz();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodScanner scanner = new MethodScanner(clazz);
    List<Method> getters = scanner.findMethods(MethodScanner.GETTERS);
    List<Method> setters = scanner.findMethods(MethodScanner.SETTERS);
    ObjectPropertyFactory propertyFactory = new ObjectPropertyFactory(lookup);
    ObjectDeserializerFactory<T> deserializerFactory = new ObjectDeserializerFactory<>(context);

    return Context.<List<ConfigError>, Deserializer<T>>either(ctx -> {
      List<ProxyProperty<T>> proxyProperties = ctx.bind(Either.traverse(getters, method -> propertyFactory.<T>from(method)
          .map(property -> new ProxyProperty<>(property, method))));

      List<ObjectProperty<?, T>> properties = new ArrayList<>();
      List<ProxyInitializer.ProxyAccessor> initializerMethods = new ArrayList<>();
      List<PropertySetter> propertySetters = new ArrayList<>();
      for (ProxyProperty<T> proxyProperty : proxyProperties) {
        ObjectProperty<?, T> property = proxyProperty.property;

        properties.add(property);
        initializerMethods.add(new ProxyInitializer.ProxyAccessor(property.name(), property.type(), proxyProperty.getter));
        setters.stream()
            .filter(setterPredicate(property))
            .findAny()
            .ifPresent(method -> propertySetters.add(new PropertySetter(property.name(), method)));
      }

      InitializerSpec<T> initializer = ProxyInitializer.from(clazz, initializerMethods, propertySetters);
      DefaultValue<T> proxy = DefaultValue.proxy(clazz, proxyProperties);

      return ctx.bind(deserializerFactory.create(properties, Collections.singletonList(initializer), proxy, type));
    }).mapLeft(errors -> new ConfigError.TypedDeserializeErrors(clazz, errors));
  }

  private static Predicate<Method> setterPredicate(ObjectProperty<?, ?> property) {
    return method -> {
      String methodName = method.getName();
      return method.getReturnType().equals(property.type().clazz())
          && (methodName.equalsIgnoreCase(property.name())
            || methodName.startsWith("set") && methodName.substring(3).equalsIgnoreCase(property.name()));
    };
  }

  public record ProxyProperty<T>(ObjectProperty<?, T> property, Method getter) {
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
