package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InstanceFieldSetter<T> implements ObjectInitializer<T> {
  private final MethodHandle constructor;
  private final List<MethodHandle> fieldSetters;

  InstanceFieldSetter(MethodHandle constructor, List<MethodHandle> fieldSetters) {
    this.constructor = constructor;
    this.fieldSetters = fieldSetters;
  }

  @Override
  public Either<ConfigError, T> initialize(List<Object> values) {
    try {
      T instance = (T) constructor.invoke();
      for (int i = 0; i < fieldSetters.size(); i++) {
        fieldSetters.get(i).invoke(instance, values.get(i));
      }

      return Either.right(instance);
    } catch (Throwable e) {
      return Either.left(new ConfigError.ObjectInitializationError(e.getMessage()));
    }
  }

  // TODO: allow setting via methods
  public static <T> Either<ConfigError, InitializerSpec<T>> from(Constructor<T> constructor, List<Field> fields) {
    if (Modifier.isPrivate(constructor.getModifiers())) {
      return Either.left(new ConfigError.InaccessibleConstructor("private constructor"));
    }

    if (constructor.getParameterCount() != 0) {
      return Either.left(null);
    }

    boolean hasInvalidFields = fields.stream().anyMatch(
        field -> Modifier.isTransient(field.getModifiers())
            || Modifier.isFinal(field.getModifiers())
            || Modifier.isStatic(field.getModifiers())
    );

    if (hasInvalidFields) {
      return Either.left(null);
    }

    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodHandle constructorHandle;

    try {
      constructorHandle = lookup.unreflectConstructor(constructor);
    } catch (IllegalAccessException e) {
      return Either.left(new ConfigError.InaccessibleConstructor(e.getMessage()));
    }

    List<MethodHandle> setters = new ArrayList<>(fields.size());
    List<InitializerArgument> arguments = new ArrayList<>(fields.size());

    for (Field field : fields) {
      ValueType<Object> fieldType = ValueType.from(field.getAnnotatedType(), List.of(field.getAnnotations()));
      try {
        field.trySetAccessible();
        setters.add(lookup.unreflectSetter(field));
        arguments.add(new InitializerArgument(field.getName(), fieldType));
      } catch (IllegalAccessException e) {
        return Either.left(new ConfigError.InaccessibleField(field.getName(), fieldType));
      }
    }

    return Either.right(new InitializerSpec<>(new InstanceFieldSetter<>(constructorHandle, setters), arguments));
  }
}
