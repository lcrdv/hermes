package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;

import java.util.Collections;
import java.util.List;

public record LocalizedNode(NodePath path, ConfigNode node) {
  public ConfigNode asNode() {
    if (path.isRoot()) {
      return node;
    }

    List<NodeKey> keys = path.keys();
    ConfigNode lastNode = node;
    for (int i = keys.size() - 1; i >= 0; i--) {
      lastNode = new MapNode(Collections.singletonList(new MapNode.Entry(keys.get(i), lastNode)));
    }

    return lastNode;
  }
}
