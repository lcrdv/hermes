package dev.lacrid.hermes.loader;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.source.loader.SourceLoader;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;

public final class SourcedValueLoader {
  private final SourceLoader sourceLoader;
  private final ValueLoader valueLoader;

  public SourcedValueLoader(SourceLoader sourceLoader, ValueLoader valueLoader) {
    this.sourceLoader = sourceLoader;
    this.valueLoader = valueLoader;
  }

  public <T> Either<ConfigError, T> load(List<ConfigSource> sources, TreeProvider treeProvider, ValueType<T> type, T defaultValue, NodePath path) {
    return loadSources(sources)
        .flatMap(treeProvider::tree)
        .flatMap(targetNode -> valueLoader.load(targetNode, type, defaultValue, path));
  }

  private Either<ConfigError, List<ConfigNode>> loadSources(List<ConfigSource> sources) {
    return Either.sequence(sourceLoader.loadSources(sources))
        .mapLeft(ConfigError.SourceErrors::new);
  }

  public interface TreeProvider {
    Either<ConfigError, ConfigNode> tree(List<ConfigNode> sources);
  }
}
