package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.deserializer.supplier.TypeSupplierFactory;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.*;

public final class SetDeserializerFactory implements DeserializerFactory<Set> {
  private final TypeSupplierFactory<Set> setSupplier;

  public SetDeserializerFactory(TypeSupplierFactory<Set> setSupplier) {
    this.setSupplier = setSupplier;
  }

  public SetDeserializerFactory() {
    setSupplier = new TypeSupplierFactory<>(() -> new LinkedHashSet<>());
    setSupplier.registerSupplier(Set.class, LinkedHashSet::new);
    setSupplier.registerSupplier(HashSet.class, HashSet::new);
    setSupplier.registerSupplier(LinkedHashSet.class, LinkedHashSet::new);
    setSupplier.registerAnnotationResolver(SetType.class, SetType::value);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Either<ConfigError, Deserializer<Set>> make(ValueType<Set> type, DeserializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <E> Either<ConfigError, Deserializer<Set<E>>> internalMake(ValueType<Set<E>> type, DeserializerContext context) {
    Optional<ValueType<E>> elementType = type.parameterType(0);
    if (elementType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return context.deserializers().find(elementType.get())
        .map(deserializer -> new CollectionDeserializer<>(deserializer, setSupplier.supplierFor(type)));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return Set.class.isAssignableFrom(type.clazz());
  }

  public @interface SetType {
    Class<? extends Set> value();
  }
}
