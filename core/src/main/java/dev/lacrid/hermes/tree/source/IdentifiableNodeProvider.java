package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.util.List;

public interface IdentifiableNodeProvider {
  Either<ConfigError, List<ConfigNode>> nodes(List<SourceId> ids);
}
