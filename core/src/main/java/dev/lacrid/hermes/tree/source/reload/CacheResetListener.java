package dev.lacrid.hermes.tree.source.reload;

import dev.lacrid.hermes.source.CachedConfigSource;

public class CacheResetListener implements SourceReloadListener {
  private final CachedConfigSource source;

  public CacheResetListener(CachedConfigSource source) {
    this.source = source;
  }

  @Override
  public void sourceReloaded() {
    this.source.reset();
  }
}
