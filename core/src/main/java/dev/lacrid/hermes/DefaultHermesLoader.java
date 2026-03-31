package dev.lacrid.hermes;

import dev.lacrid.hermes.bind.ConfigValueBinder;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.ConfigException;
import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.request.ValueBindRequest;
import dev.lacrid.hermes.request.ScopedSaveRequest;
import dev.lacrid.hermes.request.ScopedValueRequest;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.target.ConfigWriters;
import dev.lacrid.hermes.tree.Scope;
import dev.lacrid.hermes.target.ConfigOutput;
import dev.lacrid.hermes.tree.source.ReadTreeLookup;
import dev.lacrid.hermes.tree.source.SourceTrees;
import dev.lacrid.hermes.tree.source.WriteTreeLookup;
import dev.lacrid.hermes.tree.LocalizedEntry;
import dev.lacrid.lambda.Either;

import java.util.List;

final class DefaultHermesLoader implements HermesLoader {
  private final SourcedValueLoader sourcedValueLoader;
  private final ConfigValueBinder valueBinder;
  private final SourceTrees trees;
  private final ConfigWriters writers;

  DefaultHermesLoader(SourcedValueLoader sourcedValueLoader, ConfigValueBinder valueBinder, SourceTrees trees, ConfigWriters writers) {
    this.sourcedValueLoader = sourcedValueLoader;
    this.valueBinder = valueBinder;
    this.trees = trees;
    this.writers = writers;
  }

  @Override
  public <T> T load(ScopedValueRequest<T> request) {
    SourcedValueLoader.TreeProvider treeProvider = sources -> trees.readTree(new ReadTreeLookup(request.scope(), request.path(), sources));
    var result = sourcedValueLoader.load(request.sources(), treeProvider, request.type(), request.defaultValue(), request.path());
    return result.getOrElseThrow(ConfigException::new);
  }

  @Override
  public <T> T bind(ValueBindRequest<T> request) {
    return valueBinder.bind(request.type(), request.defaultValue(), request.scope(), request.path());
  }

  @Override
  public void update(Scope scope, LocalizedEntry entry) {
    trees.update(scope, entry);
  }

  @Override
  public void save(ScopedSaveRequest request) {
    WriteTreeLookup lookup = new WriteTreeLookup(request.scope(), request.path(), request.defaults(), request.updates(), request.overrides());
    trees.writeTree(lookup).flatMap(tree -> Either.traverse(request.outputs(), target -> target.write(tree, writers))
        .mapLeft(ConfigError.WritingErrors::new));
  }
}
