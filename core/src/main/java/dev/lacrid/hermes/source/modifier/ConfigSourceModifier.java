package dev.lacrid.hermes.source.modifier;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

public abstract class ConfigSourceModifier implements ConfigSource {
  private final ConfigSource delegate;

  public ConfigSourceModifier(ConfigSource delegate) {
    this.delegate = delegate;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    return delegate.loadConfig(parser)
        .flatMap(this::accept);
  }

  protected abstract Either<ConfigError, ConfigNode> accept(ConfigNode root);
}
