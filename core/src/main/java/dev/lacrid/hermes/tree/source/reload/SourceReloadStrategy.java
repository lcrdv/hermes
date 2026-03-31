package dev.lacrid.hermes.tree.source.reload;

public interface SourceReloadStrategy extends SourceReloadListener {
  @Override
  default void sourceReloaded() {
  }
}
