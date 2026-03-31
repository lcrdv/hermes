package dev.lacrid.hermes.source.loader;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.lambda.Either;

import java.util.List;

public interface SourceLoader {
  List<Either<ConfigError, ConfigNode>> loadSources(List<ConfigSource> sources);
}
