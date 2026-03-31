package dev.lacrid.hermes.request;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.tree.Scope;

import java.util.Objects;
import java.util.function.Supplier;

public record ValueBindRequest<T>(
    ValueType<T> type,
    Supplier<T> defaultValue,
    Scope scope,
    NodePath path
) {
  public ValueBindRequest {
    Objects.requireNonNull(type);
    Objects.requireNonNull(scope);
    Objects.requireNonNull(path);
  }
}
