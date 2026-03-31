package dev.lacrid.hermes.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public interface ValueType<T> {
  Type type();

  Class<T> clazz();

  <V> Optional<ValueType<V>> parameterType(int index);

  Annotations annotations();

  default <V> ValueType<V> toSubType(Class<V> subType) {
    return new BasicValueType<>(subType, annotations().subType(subType));
  }

  default boolean isEnum() {
    return clazz().isEnum();
  }

  default boolean isInterface() {
    return clazz().isInterface();
  }

  class AnnotatedValueType<T> implements ValueType<T> {
    private final AnnotatedType annotatedType;
    private final Class<T> clazz;
    private final Annotations annotations;

    AnnotatedValueType(AnnotatedType annotatedType, Class<T> clazz, Annotations annotations) {
      this.annotatedType = annotatedType;
      this.clazz = clazz;
      this.annotations = annotations;
    }

    @Override
    public Type type() {
      return annotatedType.getType();
    }

    @Override
    public Class<T> clazz() {
      return clazz;
    }

    @Override
    public <V> Optional<ValueType<V>> parameterType(int index) {
      return Optional.of(annotatedType)
          .filter(type -> type instanceof AnnotatedParameterizedType)
          .map(type -> ((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments())
          .filter(arguments -> arguments.length > index)
          .map(arguments -> ValueType.from(arguments[index], Collections.emptyList()));
    }

    @Override
    public Annotations annotations() {
      return annotations;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      AnnotatedValueType<?> that = (AnnotatedValueType<?>) o;
      return Objects.equals(annotatedType, that.annotatedType) && Objects.equals(clazz, that.clazz) && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
      return Objects.hash(annotatedType, clazz, annotations);
    }

    @Override
    public String toString() {
      return clazz.getSimpleName();
    }
  }

  class BasicValueType<T> implements ValueType<T> {
    private final Type type;
    private final Class<T> clazz;
    private final Annotations annotations;

    BasicValueType(Type type, Class<T> clazz, Annotations annotations) {
      this.type = type;
      this.clazz = clazz;
      this.annotations = annotations;
    }

    BasicValueType(Class<T> clazz, Annotations annotations) {
      this.type = clazz;
      this.clazz = clazz;
      this.annotations = annotations;
    }

    @Override
    public Type type() {
      return type;
    }

    @Override
    public Class<T> clazz() {
      return clazz;
    }

    @Override
    public <V> Optional<ValueType<V>> parameterType(int index) {
      if (!(type instanceof ParameterizedType)) {
        return Optional.empty();
      }

      Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
      if (arguments.length <= index) {
        return Optional.empty();
      }

      return Optional.of(ValueType.from(arguments[index]));
    }

    @Override
    public Annotations annotations() {
      return annotations;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      BasicValueType<?> that = (BasicValueType<?>) o;
      return Objects.equals(type, that.type) && Objects.equals(clazz, that.clazz) && Objects.equals(annotations, that.annotations);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, clazz, annotations);
    }

    @Override
    public String toString() {
      return clazz.getSimpleName();
    }
  }


  // remove
  static <T> ValueType<T> from(Field field) {
    return new AnnotatedValueType<>(field.getAnnotatedType(), (Class<T>) field.getType(),
        Annotations.of(field.getType(), List.of(field.getAnnotations())));
  }

  static <T> ValueType<T> from(Parameter parameter) {
    Class<T> clazz = (Class<T>) parameter.getType();
    return new AnnotatedValueType<>(parameter.getAnnotatedType(), clazz, Annotations.of(clazz, List.of(parameter.getAnnotations())));
  }

  static <T> ValueType<T> from(AnnotatedType annotatedType, List<Annotation> annotations) {
    Class<T> clazz = (Class<T>) ValueType.clazzFromType(annotatedType.getType());
    // List.of(annotations, annotatedType.getAnnotations())
    return new AnnotatedValueType<>(annotatedType, clazz, Annotations.of(clazz, annotations));
  }

  @SuppressWarnings("unchecked")
  static <T> ValueType<T> from(Type type) {
    Class<T> clazz = (Class<T>) ValueType.clazzFromType(type);
    return new BasicValueType<>(type, clazz, Annotations.of(clazz, Collections.emptyList()));
  }

  static <T> ValueType<T> from(Class<T> clazz) {
    return new BasicValueType<>(clazz, Annotations.of(clazz, Collections.emptyList()));
  }

  static <T> ValueType<T> from(TypeReference<T> typeReference) {
    return from(typeReference.type());
  }

  @SuppressWarnings("unchecked")
  static <T> ValueType<T> from(T object) {
    return from((Class<T>) object.getClass());
  }

  @SuppressWarnings("unchecked")
  static <T> ValueType<T> narrow(ValueType<? extends T> type) {
    return (ValueType<T>) type;
  }

  private static Class<?> clazzFromType(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) type).getRawType();
    } else if (type instanceof GenericArrayType) {
      return Array.newInstance(clazzFromType(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
    }
    return null;
  }
}
