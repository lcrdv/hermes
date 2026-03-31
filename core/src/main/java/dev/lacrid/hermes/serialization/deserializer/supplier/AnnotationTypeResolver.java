package dev.lacrid.hermes.serialization.deserializer.supplier;

import dev.lacrid.hermes.type.ValueType;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

class AnnotationTypeResolver<A extends Annotation, T> {
  private final Function<A, Class<? extends T>> resolver;
  private final Class<A> annotationType;

  AnnotationTypeResolver(Function<A, Class<? extends T>> resolver, Class<A> annotationType) {
    this.resolver = resolver;
    this.annotationType = annotationType;
  }

  Optional<Class<? extends T>> resolve(ValueType<? extends T> type) {
    return type.annotations()
        .find(annotationType)
        .map(resolver);
  }
}
