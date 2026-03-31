package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.fetcher.NodeFetcher;

import java.util.Optional;

public interface NodeWalker {
  Optional<ConfigNode> resolve(ConfigNode node, NodePath path);

  static NodeWalker visitingWalker() {
    return (node, path) -> {
      if (path.isRoot()) {
        return Optional.of(node);
      }

      if (!(node instanceof MapNode mapNode)) {
        return Optional.empty();
      }

      return NodeFetcher.exactMatch(path).fetch(mapNode);
    };
  }

  static NodeWalker mapInsertingWalker() {
    return (node, path) -> {
      ConfigNode currentNode = node;
      for (NodeKey key : path.keys()) {
        if (!(currentNode instanceof MapNode mapNode)) {
          return Optional.empty();
        }

        var result = mapNode.findByKey(key);
        if (result.isEmpty()) {
          MapNode newNode = new MapNode();
          mapNode.addEntry(new MapNode.Entry(key, newNode));
          currentNode = newNode;
        } else {
          currentNode = result.get();
        }
      }

      return Optional.of(currentNode);
    };
  }
}
