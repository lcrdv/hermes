package dev.lacrid.hermes.loader;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public final class DefaultValueLoader implements ValueLoader {
  private final Deserializers deserializers;

  public DefaultValueLoader(Deserializers deserializers) {
    this.deserializers = deserializers;
  }

  @Override
  public <T> Either<ConfigError, T> load(ConfigNode node, ValueType<T> type, T defaultValue, NodePath path) {
    return deserializers.deserialize(node, type, defaultValue);
  }
}
