package dev.lacrid.hermes.node.path;

import dev.lacrid.hermes.node.ValueNode;

public record NodeKey(String key) {
  public ValueNode asNode() {
    return ValueNode.of(key);
  }

  public NodeKey normalize() {
    return new NodeKey(key.toLowerCase());
  }

  public static NodeKey of(String key) {
    return new NodeKey(key);
  }
}
