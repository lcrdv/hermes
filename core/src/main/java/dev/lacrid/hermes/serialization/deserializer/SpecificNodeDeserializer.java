package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.lambda.Either;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;

public abstract class SpecificNodeDeserializer<NodeT extends ConfigNode, ValueT> implements Deserializer<ValueT> {
  private final Class<NodeT> requiredNode;

  protected SpecificNodeDeserializer() {
    this.requiredNode = (Class<NodeT>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  public SpecificNodeDeserializer(Class<NodeT> requiredNode) {
    this.requiredNode = requiredNode;
  }

  @Override
  public Either<ConfigError, ValueT> deserialize(ConfigNode node, ValueT defaultValue) {
    return requiredNode.isAssignableFrom(node.getClass())
        ? deserializeNode(requiredNode.cast(node), defaultValue)
        : Either.left(ConfigError.unexpectedNode(requiredNode, node));
  }

  protected abstract Either<ConfigError, ValueT> deserializeNode(NodeT node, ValueT defaultValue);
}
