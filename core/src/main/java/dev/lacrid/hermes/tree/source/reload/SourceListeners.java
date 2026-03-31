package dev.lacrid.hermes.tree.source.reload;

import dev.lacrid.hermes.tree.source.SourceId;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SourceListeners {
  private final Map<SourceId, List<WeakReference<SourceReloadListener>>> sourceReloadListeners = new ConcurrentHashMap<>();

  public void reload(SourceId sourceId) {
    listeners(sourceId).stream()
        .map(WeakReference::get)
        .filter(Objects::nonNull)
        .forEach(SourceReloadListener::sourceReloaded);
  }

  public void addListener(SourceId sourceId, SourceReloadListener listener) {
    listeners(sourceId).add(new WeakReference<>(listener));
  }

  private List<WeakReference<SourceReloadListener>> listeners(SourceId id) {
    return sourceReloadListeners.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>());
  }
}
