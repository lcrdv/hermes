package dev.lacrid.hermes.serialization.serializer.object;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.LocalizedError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.NodeInserter;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.lambda.Either;

import java.util.List;
import java.util.function.Function;

class ObjectSerializer<T> implements Serializer<T> {
  private final List<ObjectPropertySerializer<?, T>> properties;
  private final Class<T> type;

  ObjectSerializer(List<ObjectPropertySerializer<?, T>> properties, Class<T> type) {
    this.properties = properties;
    this.type = type;
  }

  @Override
  public Either<ConfigError, ConfigNode> serialize(T value) {
    return Either.traverse(properties, field -> field.serialize(value))
        .mapLeft(LocalizedError::flatten)
        .flatMap(nodes -> {
          MapNode parent = new MapNode();
          NodeInserter inserter = new NodeInserter(parent);

          return Either.traverse(nodes, node -> inserter.insert(node.path(), node.node())
              .mapLeft(error -> new LocalizedError(node.path(), error)))
              .biMap(LocalizedError::flatten, v -> parent);
        })
        .biMap(errors -> new ConfigError.TypedSerializeErrors(type, errors), Function.identity());
  }
}
