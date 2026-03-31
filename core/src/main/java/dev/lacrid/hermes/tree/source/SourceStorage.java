package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.tree.Scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceStorage {
  private final List<IdentifiableSource> sources;
  private final Map<SourceId, IdentifiableSource> sourcesById = new HashMap<>();

  public SourceStorage(List<IdentifiableSource> sources) {
    this.sources = new ArrayList<>(sources);
    this.sources.forEach(source -> sourcesById.put(source.id(), source));
  }

  public List<SourceId> idsByScope(Scope scope) {
    return sources.stream()
        .filter(source -> scope.includes(source.tags()))
        .map(IdentifiableSource::id)
        .toList();
  }

  public List<ConfigSource> sources(List<SourceId> ids) {
    List<ConfigSource> sources = new ArrayList<>();
    for (SourceId id : ids) {
      sources.add(sourcesById.get(id).source());
    }
    return sources;
  }
}
