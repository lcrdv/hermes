package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.loader.SourceLoader;
import dev.lacrid.lambda.Either;

import java.util.List;

public class LoadingNodeProvider implements IdentifiableNodeProvider {
  private final SourceStorage storage;
  private final SourceLoader loader;

  public LoadingNodeProvider(SourceStorage storage, SourceLoader loader) {
    this.storage = storage;
    this.loader = loader;
  }

  @Override
  public Either<ConfigError, List<ConfigNode>> nodes(List<SourceId> ids) {
    return Either.sequence(loader.loadSources(storage.sources(ids))).mapLeft(ConfigError.SourceErrors::new);
  }
}
