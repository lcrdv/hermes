package dev.lacrid.hermes.source;

import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;

public interface ConfigSource {
  Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser);
}
