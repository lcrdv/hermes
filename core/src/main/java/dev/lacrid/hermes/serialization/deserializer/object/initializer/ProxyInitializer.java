package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.PropertySetter;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class ProxyInitializer<T> implements ObjectInitializer<T> {
  private final Class<T> clazz;
  private final Map<Method, String> methodToProperty;
  private final List<String> properties;

  ProxyInitializer(Class<T> clazz, Map<Method, String> methodToProperty, List<String> properties) {
    this.clazz = clazz;
    this.methodToProperty = methodToProperty;
    this.properties = properties;
  }

  @Override
  public Either<ConfigError, T> initialize(List<Object> values) {
    ProxyValuesHolder proxyValuesHolder = new ProxyValuesHolder(methodToProperty);

    for (int i = 0; i < values.size(); i++) {
      proxyValuesHolder.propertyToValue.put(properties.get(i), values.get(i));
    }

    T instance = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, proxyValuesHolder);
    return Either.right(instance);
  }

  public static <T> InitializerSpec<T> from(Class<T> type, List<ProxyAccessor> methods, List<PropertySetter> setters) {
    List<InitializerArgument> arguments = new ArrayList<>();
    List<String> properties = new ArrayList<>();
    Map<Method, String> methodToProperty = new HashMap<>();

    methods.forEach(method -> {
      arguments.add(new InitializerArgument(method.property, method.type));
      properties.add(method.property);
      methodToProperty.put(method.accessor, method.property);
    });

    setters.forEach(setter -> methodToProperty.put(setter.method(), setter.property()));

    return new InitializerSpec<>(new ProxyInitializer<>(type, methodToProperty, properties), arguments);
  }

  private static class ProxyValuesHolder implements InvocationHandler {
    private final Map<Method, String> methodToProperty;
    private final Map<String, Object> propertyToValue;

    ProxyValuesHolder(Map<Method, String> methodToProperty) {
      this.methodToProperty = methodToProperty;
      this.propertyToValue = new HashMap<>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      String property = methodToProperty.get(method);
      if (property == null) {
        throw new UnsupportedOperationException("failed to resolve property from " + method.getName());
      }

      if (args == null || args.length == 0) {
        return propertyToValue.get(property);
      } else {
        propertyToValue.put(property, args[0]);
        return null;
      }
    }
  }

  public record ProxyAccessor(String property, ValueType<?> type, Method accessor) {
  }
}
