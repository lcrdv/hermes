package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public sealed interface LocalizedEntry {
  record ValueEntry(LocalizedValue<Object> entry) implements LocalizedEntry {
  }

  record NodeEntry(LocalizedNode entry) implements LocalizedEntry {
  }

  default Either<ConfigError, LocalizedNode> asLocalizedNode(Serializers serializers) {
    return switch (this) {
      case LocalizedEntry.NodeEntry(LocalizedNode node) -> Either.right(node);
      case LocalizedEntry.ValueEntry(LocalizedValue(NodePath path, Object value, ValueType<Object> type)) ->
          serializers.find(type)
              .flatMap(serializer -> serializer.serialize(value))
              .map(node -> new LocalizedNode(path, node));
    };
  }
}
