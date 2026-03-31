package dev.lacrid.hermes.loader;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public interface ValueLoader {
  <T> Either<ConfigError, T> load(ConfigNode node, ValueType<T> type, T defaultValue, NodePath path);
}
