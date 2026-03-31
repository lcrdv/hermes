package dev.lacrid.hermes.type;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Annotations {
  private final List<Annotation> typeAnnotations;
  private final List<Annotation> fieldAnnotations;
  private final Map<Class<? extends Annotation>, Annotation> annotationByType;

  Annotations(List<Annotation> typeAnnotations, List<Annotation> fieldAnnotations) {
    this.typeAnnotations = typeAnnotations;
    this.fieldAnnotations = fieldAnnotations;
    this.annotationByType = Stream.concat(typeAnnotations.stream(), fieldAnnotations.stream())
        .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));
  }

  public <V extends Annotation> Optional<V> find(Class<V> annotationClass) {
    return Optional.ofNullable(annotationByType.get(annotationClass))
        .map(annotationClass::cast);
  }

  public boolean has(Class<? extends Annotation> annotationClass) {
    return annotationByType.containsKey(annotationClass);
  }

  public <V extends Annotation> List<V> findAll(Class<V> annotationClass) {
    return Collections.emptyList();
  }

  Annotations subType(Class<?> clazz) {
    return of(clazz, fieldAnnotations);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Annotations that = (Annotations) o;
    return Objects.equals(typeAnnotations, that.typeAnnotations)
        && Objects.equals(fieldAnnotations, that.fieldAnnotations)
        && Objects.equals(annotationByType, that.annotationByType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeAnnotations, fieldAnnotations, annotationByType);
  }

  public static Annotations of(Class<?> clazz, List<Annotation> annotations) {
    return new Annotations(List.of(clazz.getAnnotations()), annotations);
  }
}
