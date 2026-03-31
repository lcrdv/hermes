package dev.lacrid.hermes.source.modifier;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.lambda.Either;

public final class PrefixedConfigSource extends ConfigSourceModifier {
  private final NodePath prefix;

  public PrefixedConfigSource(ConfigSource delegate, NodePath prefix) {
    super(delegate);
    this.prefix = prefix;
  }

  @Override
  protected Either<ConfigError, ConfigNode> accept(ConfigNode root) {
    return Either.right(new LocalizedNode(prefix, root).asNode());
  }
}
