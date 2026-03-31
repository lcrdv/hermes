package dev.lacrid.hermes.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.naming.NamingStrategies;
import dev.lacrid.hermes.naming.RegexLexer;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

import java.util.Collections;

public final class EnvironmentVariablesSource extends ConfigurableSource<EnvironmentVariablesSource> {
  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    return parser.parse(System.getenv(), format());
  }

  @Override
  protected EnvironmentVariablesSource self() {
    return this;
  }

  @Override
  protected PathResolver basePathResolver() {
    return PathResolver.from(NamingStrategies.PLAIN, new RegexLexer(), Collections::singletonList);
  }
}
