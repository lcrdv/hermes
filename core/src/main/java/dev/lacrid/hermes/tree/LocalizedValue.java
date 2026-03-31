package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.type.ValueType;

public record LocalizedValue<T>(NodePath path, T value, ValueType<T> type) {
}
