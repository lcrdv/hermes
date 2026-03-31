package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;
import dev.lacrid.hermes.error.ConfigError;

import java.io.InputStream;
import java.io.Reader;
import java.util.Map;

public interface SourceParsers {
  Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format);

  Either<ConfigError, ConfigNode> parse(Map<String, String> input, SourceFormat format);

  <T> Either<ConfigError, ConfigNode> parse(T value, ValueType<T> valueType);
}
