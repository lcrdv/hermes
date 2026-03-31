package dev.lacrid.hermes.serialization.deserializer.object;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.serialization.PropertyAccessor;
import dev.lacrid.hermes.error.LocalizedError;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.lambda.Either;

import java.util.List;

class ObjectPropertyDeserializer<P, T> {
  private final Deserializer<P> deserializer;
  private final PropertyAccessor<P, T> accessor;
  private final int index;

  private final NodePath path;

  ObjectPropertyDeserializer(Deserializer<P> deserializer, PropertyAccessor<P, T> accessor, int index, NodePath path) {
    this.deserializer = deserializer;
    this.accessor = accessor;
    this.index = index;
    this.path = path;
  }

  Either<LocalizedError, P> deserialize(List<ConfigNode> nodes, T target) {
    P defaultValue = accessor.resolve(target);
    return deserializer.deserialize(nodes.get(index), defaultValue)
        .mapLeft(error -> new LocalizedError(path, error));
  }
}
