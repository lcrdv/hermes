package dev.lacrid.hermes.bind;

import dev.lacrid.hermes.error.ConfigException;
import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.tree.source.ReadTreeLookup;
import dev.lacrid.hermes.tree.source.SourceTrees;
import dev.lacrid.hermes.tree.source.reload.SourceReloadListener;
import dev.lacrid.hermes.type.ValueType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.Supplier;

class BoundValueHandler<T> implements InvocationHandler, SourceReloadListener {
  private final SourceTrees trees;
  private final SourcedValueLoader loader;
  private final ReadTreeLookup lookup;
  private final ValueType<T> type;
  private final Supplier<T> defaultValue;
  private final NodePath path;

  private T cachedValue = null;

  BoundValueHandler(SourceTrees trees, SourcedValueLoader loader, ReadTreeLookup lookup, ValueType<T> type, Supplier<T> defaultValue, NodePath path) {
    this.trees = trees;
    this.loader = loader;
    this.lookup = lookup;
    this.type = type;
    this.defaultValue = defaultValue;
    this.path = path;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (cachedValue == null) {
      load();
    }

    return method.invoke(cachedValue);
  }

  void load() {
    var value = loader.load(Collections.emptyList(),
        sources -> trees.readTree(lookup), type, defaultValue.get(), path);
    value.handle(
        left -> { throw new ConfigException(left); },
        right -> cachedValue = right
    );
  }

  @Override
  public void sourceReloaded() {
    cachedValue = null;
  }
}
