package dev.lacrid.hermes.source.modifier;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodeWalker;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.lambda.Either;

public final class TraversingConfigSource extends ConfigSourceModifier {
  private static final NodeWalker WALKER = NodeWalker.visitingWalker();

  private final NodePath path;

  public TraversingConfigSource(ConfigSource delegate, NodePath path) {
    super(delegate);
    this.path = path;
  }

  @Override
  protected Either<ConfigError, ConfigNode> accept(ConfigNode root) {
    return WALKER.resolve(root, path).map(node -> Either.<ConfigError, ConfigNode>right(root))
        .orElseGet(() -> Either.left(new ConfigError.MissingNode()));
  }
}
