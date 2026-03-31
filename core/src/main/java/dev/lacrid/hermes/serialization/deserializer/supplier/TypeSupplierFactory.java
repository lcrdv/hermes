package dev.lacrid.hermes.serialization.deserializer.supplier;

import dev.lacrid.hermes.type.ValueType;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypeSupplierFactory<T> {
  private final Map<Class<? extends T>, TypeSupplier<T>> suppliers = new HashMap<>();
  private final List<AnnotationTypeResolver<?, T>> annotationResolvers = new ArrayList<>();
  private final TypeSupplier<T> fallbackSupplier;

  public TypeSupplierFactory(TypeSupplier<T> fallbackSupplier) {
    this.fallbackSupplier = fallbackSupplier;
  }

  public TypeSupplierFactory(Supplier<? extends T> fallbackSupplier) {
    this.fallbackSupplier = k -> fallbackSupplier.get();
  }

  public <V extends T> Supplier<V> supplierFor(ValueType<V> type) {
    TypeSupplier<T> supplier = typeSupplierFor(type);
    return () -> (V) supplier.create(type);
  }

  private TypeSupplier<T> typeSupplierFor(ValueType<? extends T > type) {
    TypeSupplier<T> supplier = suppliers.get(resolveCollectionType(type));
    if (supplier == null) {
      supplier = fallbackSupplier;
    }

    return supplier;
  }

  private Class<? extends T> resolveCollectionType(ValueType<? extends T> type) {
    for (AnnotationTypeResolver<?, T> annotationResolver : annotationResolvers) {
      Optional<Class<? extends T>> annotationResult = annotationResolver.resolve(type);
      if (annotationResult.isPresent()) {
        return annotationResult.get();
      }
    }

    return type.clazz();
  }

  public <A extends Annotation> void registerAnnotationResolver(Class<A> annotationType, Function<A, Class<? extends T>> resolver) {
    annotationResolvers.add(new AnnotationTypeResolver<>(resolver, annotationType));
  }

  public void registerSupplier(Class<? extends T> type, Supplier<? extends T> supplier) {
    suppliers.put(type, valueType -> supplier.get());
  }

  public void registerTypeSupplier(Class<? extends T> type, TypeSupplier<T> supplier) {
    suppliers.put(type, supplier);
  }
}

