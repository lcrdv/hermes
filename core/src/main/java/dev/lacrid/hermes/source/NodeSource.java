package dev.lacrid.hermes.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

public final class NodeSource implements ConfigSource {
  private final ConfigNode source;

  public NodeSource(ConfigNode source) {
    this.source = source;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    return Either.right(source);
  }
}
