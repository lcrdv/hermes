package dev.lacrid.hermes.tree.source.reload;

public interface ReloadStrategyFactory {
  SourceReloadStrategy create(ReloadHook reloadHook);

  static ReloadStrategyFactory loadOnce() {
    SourceReloadStrategy reloadStrategy = new SourceReloadStrategy() {};
    return reloadHook -> reloadStrategy;
  }
}
