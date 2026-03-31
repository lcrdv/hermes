package dev.lacrid.hermes.source.compose;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

public final class FallbackConfigSource implements ConfigSource {
  private final ConfigSource defaultSource;
  private final ConfigSource fallbackSource;

  public FallbackConfigSource(ConfigSource defaultSource, ConfigSource fallbackSource) {
    this.defaultSource = defaultSource;
    this.fallbackSource = fallbackSource;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    return defaultSource.loadConfig(parser)
        .flatMapLeft(error -> fallbackSource.loadConfig(parser));
  }
}
