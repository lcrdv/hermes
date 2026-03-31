package dev.lacrid.hermes.node.path.fetcher;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;

import java.util.List;
import java.util.Optional;

public interface NodeFetcher {
  Optional<ConfigNode> fetch(MapNode node);

  static NodeFetcher exactMatch(NodeKey key) {
    return node -> node.findByKey(key);
  }

  static NodeFetcher exactMatch(NodePath path) {
    List<NodeKey> keys = path.keys();
    if (keys.size() == 1) {
      return exactMatch(keys.getFirst());
    }

    return sequential(path.keys().stream()
        .map(NodeFetcher::exactMatch)
        .toList()
    );
  }

  static NodeFetcher sequential(List<NodeFetcher> fetchers) {
    return new SequentialNodeFetcher(fetchers);
  }
}
