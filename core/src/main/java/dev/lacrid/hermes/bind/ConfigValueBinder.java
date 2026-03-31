package dev.lacrid.hermes.bind;

import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.tree.source.SourceId;
import dev.lacrid.hermes.tree.source.reload.SourceListeners;
import dev.lacrid.hermes.tree.source.ReadTreeLookup;
import dev.lacrid.hermes.tree.source.SourceStorage;
import dev.lacrid.hermes.tree.source.SourceTrees;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.tree.Scope;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.function.Supplier;

public final class ConfigValueBinder {
  private final SourceStorage sourceStorage;
  private final SourceTrees trees;
  private final SourcedValueLoader loader;
  private final SourceListeners reloads;

  public ConfigValueBinder(SourceStorage sourceStorage, SourceTrees trees, SourcedValueLoader loader, SourceListeners reloads) {
    this.sourceStorage = sourceStorage;
    this.trees = trees;
    this.loader = loader;
    this.reloads = reloads;
  }

  public <T> T bind(ValueType<T> type, Supplier<T> defaultValue, Scope scope, NodePath path) {
    ReadTreeLookup lookup = new ReadTreeLookup(scope, path, Collections.emptyList());
    BoundValueHandler<T> handler = new BoundValueHandler<>(trees, loader, lookup, type, defaultValue, path);
    handler.load();

    Class<T> clazz = type.clazz();
    T instance = clazz.cast(Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler));

    for (SourceId id : sourceStorage.idsByScope(scope)) {
      reloads.addListener(id, handler);
    }

    return instance;
  }
}
