package dev.lacrid.hermes.source;

import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;

public final class ObjectConfigSource<T> implements ConfigSource {
  private final T instance;
  private final ValueType<T> type;

  public ObjectConfigSource(T instance, ValueType<T> type) {
    this.instance = instance;
    this.type = type;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    return parser.parse(instance, type);
  }
}
