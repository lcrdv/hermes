package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.tree.source.reload.SourceReloadStrategy;

import java.util.List;

public record IdentifiableSource(SourceId id, ConfigSource source, List<SourceReloadStrategy> reloadStrategies, Tags tags) {

}
