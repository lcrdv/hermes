package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.lambda.Either;

import java.util.function.Supplier;

// TODO: needs update
public final class OptionalValueDeserializer<T> implements Deserializer<T> {
  private final Deserializer<T> deserializer;
  private final Supplier<ConfigNode> fallbackNode;
  private final boolean forceDefaultsWithEmptyNode;

  public OptionalValueDeserializer(Deserializer<T> deserializer, Supplier<ConfigNode> fallbackNode, boolean forceDefaultsWithEmptyNode) {
    this.deserializer = deserializer;
    this.fallbackNode = fallbackNode;
    this.forceDefaultsWithEmptyNode = forceDefaultsWithEmptyNode;
  }

  @Override
  public Either<ConfigError, T> deserialize(ConfigNode receivedNode, T defaultValue) {
    if (receivedNode instanceof ValueNode valueNode && valueNode.holder() instanceof ValueHolder.NullHolder) {
      return Either.right(null);
    }

    ConfigNode node = selectNode(receivedNode);
    if (node instanceof NullNode) {
      if (forceDefaultsWithEmptyNode) {
        return Either.right(defaultValue);
      }

      return deserializer.deserialize(node, defaultValue)
          .flatMapLeft(error -> Either.right(defaultValue));
    }

    return deserializer.deserialize(node, defaultValue);
  }

  private ConfigNode selectNode(ConfigNode receivedNode) {
    if (fallbackNode != null && receivedNode instanceof NullNode) {
      return fallbackNode.get();
    }
    return receivedNode;
  }
}
