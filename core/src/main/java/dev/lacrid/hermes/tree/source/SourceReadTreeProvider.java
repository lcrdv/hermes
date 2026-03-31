package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.tree.source.reload.SourceListeners;
import dev.lacrid.hermes.processor.ProcessorPipeline;
import dev.lacrid.hermes.tree.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SourceReadTreeProvider {
  private final TreeFactoryProvider treeFactories;
  private final ProcessorPipeline processors;
  private final SourceListeners reloads;

  private final Map<Scope, SourceReadTree> trees = new ConcurrentHashMap<>();

  SourceReadTreeProvider(TreeFactoryProvider treeFactories, ProcessorPipeline processors, SourceListeners reloads) {
    this.treeFactories = treeFactories;
    this.processors = processors;
    this.reloads = reloads;
  }

  SourceReadTree tree(Scope scope) {
    return trees.computeIfAbsent(scope, k -> {
      TreeFactory treeFactory = treeFactories.tree(scope);
      SourceReadTree sourceReadTree = new SourceReadTree(treeFactory, processors);

      for (SourceId source : treeFactory.sources()) {
        reloads.addListener(source, sourceReadTree);
      }

      return sourceReadTree;
    });
  }

}
