package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.serialization.deserializer.supplier.TypeSupplierFactory;
import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.type.ValueType;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListDeserializerFactory implements DeserializerFactory<List> {
  private final TypeSupplierFactory<List> listSupplier;

  {
    listSupplier = new TypeSupplierFactory<>(() -> new ArrayList<>());
    listSupplier.registerSupplier(List.class, ArrayList::new);
    listSupplier.registerSupplier(ArrayList.class, ArrayList::new);
    listSupplier.registerSupplier(LinkedList.class, LinkedList::new);
    listSupplier.registerSupplier(CopyOnWriteArrayList.class, CopyOnWriteArrayList::new);
    listSupplier.registerAnnotationResolver(ListType.class, ListType::value);
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Either<ConfigError, Deserializer<List>> make(ValueType<List> type, DeserializerContext context) {
    return internalMake((ValueType) type, context);
  }

  private <E> Either<ConfigError, Deserializer<List<E>>> internalMake(ValueType<List<E>> type, DeserializerContext context) {
    Optional<ValueType<E>> elementType = type.parameterType(0);
    if (elementType.isEmpty()) {
      return Either.left(new ConfigError.UndefinedParameterType(type.clazz()));
    }

    return context.deserializers().find(elementType.get())
        .map(deserializer -> new CollectionDeserializer<>(deserializer, listSupplier.supplierFor(type)));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return List.class.isAssignableFrom(type.clazz());
  }

  public @interface ListType {
    Class<? extends List> value();
  }
}
