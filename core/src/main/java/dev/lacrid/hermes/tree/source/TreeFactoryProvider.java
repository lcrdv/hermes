package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.tree.Scope;
import dev.lacrid.hermes.tree.TreeCompiler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TreeFactoryProvider {
  private final SourceStorage sourceStorage;
  private final IdentifiableNodeProvider identifiableNodeProvider;
  private final TreeCompiler treeCompiler;

  private final Map<Scope, TreeFactory> sourceTrees = new ConcurrentHashMap<>();

  TreeFactoryProvider(SourceStorage sourceStorage, IdentifiableNodeProvider identifiableNodeProvider, TreeCompiler treeCompiler) {
    this.sourceStorage = sourceStorage;
    this.identifiableNodeProvider = identifiableNodeProvider;
    this.treeCompiler = treeCompiler;
  }

  TreeFactory tree(Scope scope) {
    return sourceTrees.computeIfAbsent(scope, k -> {
      List<SourceId> sourceIds = sourceStorage.idsByScope(k);
      return new TreeFactory(sourceIds, identifiableNodeProvider, treeCompiler);
    });
  }
}
