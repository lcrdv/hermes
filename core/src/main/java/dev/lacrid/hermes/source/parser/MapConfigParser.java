package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.lambda.Either;

import java.util.Map;

final class MapConfigParser {
  static Either<ConfigError, ConfigNode> parse(Map<String, String> input, SourceFormat format) {
    MapNode root = new MapNode();
    NodeInserter inserter = new NodeInserter(root);

    for (Map.Entry<String, String> entry : input.entrySet()) {
      NodePath path = format.pathResolver().resolve(entry.getKey());
      var result = inserter.insert(path, ValueNode.of(entry.getValue()));
      if (result.isLeft()) {
        return Either.left(result.getLeft());
      }
    }

    return Either.right(root);
  }
}
