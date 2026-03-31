package dev.lacrid.hermes.serialization.serializer.object;

import dev.lacrid.hermes.error.LocalizedError;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.serialization.PropertyAccessor;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.lambda.Either;

class ObjectPropertySerializer<P, T> {
  private final Serializer<P> serializer;
  private final PropertyAccessor<P, T> accessor;
  private final NodePath path;

  ObjectPropertySerializer(Serializer<P> serializer, PropertyAccessor<P, T> accessor, NodePath path) {
    this.serializer = serializer;
    this.accessor = accessor;
    this.path = path;
  }

  Either<LocalizedError, LocalizedNode> serialize(T value) {
    return serializer.serialize(accessor.resolve(value))
        .biMap(
            error -> new LocalizedError(path, error),
            node -> new LocalizedNode(path, node)
        );
  }
}
