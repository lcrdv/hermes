package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.lambda.Either;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public final class CollectionDeserializer<CollectionT extends Collection<E>, E> implements Deserializer<CollectionT> {
  private final Deserializer<E> elementDeserializer;
  private final Supplier<CollectionT> collectionFactory;

  public CollectionDeserializer(Deserializer<E> elementDeserializer, Supplier<CollectionT> collectionFactory) {
    this.elementDeserializer = elementDeserializer;
    this.collectionFactory = collectionFactory;
  }

  @Override
  public Either<ConfigError, CollectionT> deserialize(ConfigNode node, CollectionT defaultValue) {
    switch (node) {
      case ListNode listNode -> {
        return Either.traverse(listNode.elements(), element -> elementDeserializer.deserialize(element, null))
            .biMap(ConfigError.CollectionDeserializeErrors::new, this::createCollection);
      }
      case MapNode mapNode -> {
        return Either.traverse(mapNode.nodes(), element -> elementDeserializer.deserialize(element, null))
            .biMap(ConfigError.CollectionDeserializeErrors::new, this::createCollection);
      }
      case ValueNode ignored -> {
        return Either.left(ConfigError.unexpectedNode(ListNode.class, node));
      }
      case NullNode ignored -> {
        return Either.left(ConfigError.unexpectedNode(ListNode.class, node));
      }
    }
  }

  private CollectionT createCollection(Collection<E> values) {
    CollectionT collection = collectionFactory.get();
    collection.addAll(values);
    return collection;
  }
}
