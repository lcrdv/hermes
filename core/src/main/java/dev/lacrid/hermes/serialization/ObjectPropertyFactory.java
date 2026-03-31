package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.List;

public class ObjectPropertyFactory {
  private final MethodHandles.Lookup lookup;

  public ObjectPropertyFactory(MethodHandles.Lookup lookup) {
    this.lookup = lookup;
  }

  public <T> Either<ConfigError, ObjectProperty<?, T>> from(Field field) {
    ValueType<?> type = ValueType.from(field);
    String name = field.getName();
    try {
      field.trySetAccessible();
      MethodHandle accessorHandle = lookup.unreflectGetter(field);
      return Either.right(new ObjectProperty<>(name, PropertyAccessor.fromMethodHandle(accessorHandle), type));
    } catch (IllegalAccessException e) {
      return Either.left(new ConfigError.InaccessibleField(field.getName(), type));
    }
  }

  public <T> Either<ConfigError, ObjectProperty<?, T>> from(RecordComponent component) {
    ValueType<?> componentType = ValueType.from(component.getAnnotatedType(), List.of(component.getAnnotations()));
    String name = component.getName();
    try {
      MethodHandle accessorHandle = lookup.unreflect(component.getAccessor());
      return Either.right(new ObjectProperty<>(name, PropertyAccessor.fromMethodHandle(accessorHandle), componentType));
    } catch (IllegalAccessException e) {
      return Either.left(new ConfigError.InaccessibleRecordComponent(name, componentType));
    }
  }

  public <T> Either<ConfigError, ObjectProperty<?, T>> from(Method method) {
    ValueType<?> componentType = ValueType.from(method.getAnnotatedReturnType(), List.of(method.getAnnotations()));
    String name = simplifyMethodName(method);
    try {
      MethodHandle accessorHandle = lookup.unreflect(method);
      return Either.right(new ObjectProperty<>(name, PropertyAccessor.fromMethodHandle(accessorHandle), componentType));
    } catch (IllegalAccessException e) {
      return Either.left(new ConfigError.InaccessibleMethod(name, componentType));
    }
  }

  private static String simplifyMethodName(Method method) {
    String name = method.getName();
    if (name.startsWith("get")) {
      return name.substring(3);
    } else if (name.startsWith("is") && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
      return name.substring(2);
    }
    return name;
  }
}
