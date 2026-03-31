package dev.lacrid.hermes.node;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.lambda.Either;

import java.util.Optional;

public class NodeInserter {
  private static final NodeWalker TRAVERSAL = NodeWalker.mapInsertingWalker();

  private final MapNode node;

  public NodeInserter(MapNode node) {
    this.node = node;
  }

  public Either<ConfigError, Void> insert(NodePath path, ConfigNode value) {
    if (path.isRoot()) {
      return Either.left(new ConfigError.UnexpectedRootPath());
    }

    Optional<MapNode> parentMap = TRAVERSAL.resolve(node, path.parent())
        .filter(traversedNode -> traversedNode instanceof MapNode)
        .map(traversedNode -> (MapNode) traversedNode);

    if (parentMap.isEmpty()) {
      return Either.left(null);
    }

    parentMap.get().addEntry(new MapNode.Entry(path.lastKey(), value));
    return Either.right(null);
  }
}
