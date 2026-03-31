package dev.lacrid.hermes.node.path;

import dev.lacrid.hermes.type.ValueType;

public interface TypedPathResolverFactory {
  TypedPathResolver create(ValueType<?> type);
}
