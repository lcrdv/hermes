package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.tree.source.reload.ReloadStrategyFactory;

import java.util.List;

public record ReusableConfigSource(ConfigSource configSource, List<ReloadStrategyFactory> reloadStrategies, Tags tags) {
}
