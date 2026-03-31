package dev.lacrid.hermes.request;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.tree.Scope;

import java.util.List;
import java.util.Objects;

public record ScopedValueRequest<T>(
    List<ConfigSource> sources,
    ValueType<T> type,
    T defaultValue,
    NodePath path,
    Scope scope
) {
  public ScopedValueRequest {
    Objects.requireNonNull(sources);
    Objects.requireNonNull(type);
    Objects.requireNonNull(path);
    Objects.requireNonNull(scope);
  }

  public static class Builder {


  }
}
