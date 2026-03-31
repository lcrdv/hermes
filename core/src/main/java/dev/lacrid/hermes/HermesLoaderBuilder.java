package dev.lacrid.hermes;

import dev.lacrid.hermes.bind.ConfigValueBinder;
import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.loader.ValueLoader;
import dev.lacrid.hermes.node.NodeWalker;
import dev.lacrid.hermes.processor.SourceProcessor;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.source.CachedConfigSource;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.source.loader.SourceLoader;
import dev.lacrid.hermes.target.ConfigWriters;
import dev.lacrid.hermes.target.DefaultConfigWriters;
import dev.lacrid.hermes.tree.TreeCompiler;
import dev.lacrid.hermes.tree.processor.ProcessorTag;
import dev.lacrid.hermes.tree.processor.TaggedProcessor;
import dev.lacrid.hermes.tree.processor.TreeProcessors;
import dev.lacrid.hermes.tree.source.*;
import dev.lacrid.hermes.tree.source.reload.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HermesLoaderBuilder extends BaseHermesBuilder<HermesLoaderBuilder> {
  private final List<ReusableConfigSource> sources = new ArrayList<>();

  public HermesLoaderBuilder savingBaseProcessor(SourceProcessor processor) {
    this.sourceProcessors.add(new TaggedProcessor(processor, ProcessorTag.WRITE_BASE));
    return self();
  }

  public HermesLoaderBuilder source(ConfigSource source) {
    return source(new ReusableConfigSource(source, Collections.emptyList(), Tags.empty()));
  }

  public HermesLoaderBuilder source(ReusableConfigSource source) {
    sources.add(source);
    return this;
  }

  @Override
  protected HermesLoaderBuilder self() {
    return this;
  }

  public HermesLoader build() {
    Deserializers deserializers = deserializers();
    Serializers serializers = serializers();

    ValueLoader valueLoader = valueLoader(deserializers);
    SourceLoader sourceLoader = sourceLoader(serializers);
    SourcedValueLoader sourcedValueLoader = new SourcedValueLoader(sourceLoader, valueLoader);

    SourceListeners listeners = new SourceListeners();
    List<IdentifiableSource> identifiableSources = new ArrayList<>();
    for (ReusableConfigSource source : sources) {
      SourceId id = SourceId.newOne();
      CachedConfigSource cachedSource = new CachedConfigSource(source.configSource());
      ReloadHook hook = () -> listeners.reload(id);
      List<SourceReloadStrategy> reloadStrategies = new ArrayList<>();

      for (ReloadStrategyFactory reloadStrategy : source.reloadStrategies()) {
        reloadStrategies.add(reloadStrategy.create(hook));
      }

      listeners.addListener(id, new CacheResetListener(cachedSource));
      reloadStrategies.forEach(strategy -> listeners.addListener(id, strategy));

      identifiableSources.add(new IdentifiableSource(id, cachedSource, reloadStrategies, source.tags()));
    }

    SourceStorage sourceStorage = new SourceStorage(identifiableSources);
    TreeProcessors treeProcessors = treeProcessors();
    TreeCompiler treeCompiler = TreeCompiler.copyingCompiler();
    NodeWalker nodeWalker = NodeWalker.visitingWalker();
    IdentifiableNodeProvider nodeProvider = new LoadingNodeProvider(sourceStorage, sourceLoader);
    SourceTrees trees = new SourceTrees(sourceStorage, treeCompiler, nodeProvider, listeners, treeProcessors, nodeWalker, deserializers, serializers);

    ConfigValueBinder bindFactory = new ConfigValueBinder(sourceStorage, trees, sourcedValueLoader, listeners);
    ConfigWriters writers = new DefaultConfigWriters(this.writers);

    return new DefaultHermesLoader(sourcedValueLoader, bindFactory, trees, writers);
  }
}
