package dev.lacrid.hermes.source.loader;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

import java.util.ArrayList;
import java.util.List;

public final class SequentialSourceLoader implements SourceLoader {
  private final SourceParsers parsers;

  public SequentialSourceLoader(SourceParsers parsers) {
    this.parsers = parsers;
  }

  @Override
  public List<Either<ConfigError, ConfigNode>> loadSources(List<ConfigSource> sources) {
    List<Either<ConfigError, ConfigNode>> result = new ArrayList<>();
    for (ConfigSource source : sources) {
      result.add(source.loadConfig(parsers));
    }
    return result;
  }
}
