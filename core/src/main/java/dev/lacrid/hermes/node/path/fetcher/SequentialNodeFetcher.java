package dev.lacrid.hermes.node.path.fetcher;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;

import java.util.List;
import java.util.Optional;

public final class SequentialNodeFetcher implements NodeFetcher {
  private final List<NodeFetcher> fetchers;

  public SequentialNodeFetcher(List<NodeFetcher> fetchers) {
    this.fetchers = fetchers;
  }

  @Override
  public Optional<ConfigNode> fetch(MapNode parent) {
    ConfigNode currentNode = parent;
    for (NodeFetcher fetcher : fetchers) {
      if (!(currentNode instanceof MapNode mapNode)) {
        return Optional.empty();
      }

      currentNode = fetcher.fetch(mapNode)
          .orElse(null);
    }

    return Optional.ofNullable(currentNode);
  }
}
