package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.tree.TreeCompiler;
import dev.lacrid.lambda.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class TreeFactory {
  private final List<SourceId> sources;
  private final IdentifiableNodeProvider sourceProvider;
  private final TreeCompiler treeCompiler;

  private final List<LocalizedNode> updates = new CopyOnWriteArrayList<>();

  TreeFactory(List<SourceId> sources, IdentifiableNodeProvider sourceProvider, TreeCompiler treeCompiler) {
    this.sources = sources;
    this.sourceProvider = sourceProvider;
    this.treeCompiler = treeCompiler;
  }

  Either<ConfigError, ConfigNode> create(TreeComponents components) {
    return sourceProvider.nodes(sources).flatMap(sources -> {
      List<ConfigNode> nodes = new ArrayList<>(components.preUpdates().size() + sources.size() + components.postUpdates().size());

      nodes.addAll(components.preUpdates());
      nodes.addAll(sources);
      nodes.addAll(components.postUpdates());

      List<LocalizedNode> overrides = new ArrayList<>(updates);
      overrides.addAll(components.overrides());

      return treeCompiler.build(nodes, overrides);
    });
  }

  void update(LocalizedNode node) {
    updates.add(node);
  }

  List<SourceId> sources() {
    return sources;
  }
}
