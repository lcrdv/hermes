package dev.lacrid.hermes.node.path;

import dev.lacrid.hermes.type.ValueType;

public interface TypedPathResolver {
  NodePath resolve(String name, ValueType<?> type);
}
