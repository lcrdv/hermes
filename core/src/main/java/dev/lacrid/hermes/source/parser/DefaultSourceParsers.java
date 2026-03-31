package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.io.InputStream;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DefaultSourceParsers implements SourceParsers {
  private final List<SourceParser> parsers;
  private final Serializers serializers;

  public DefaultSourceParsers(List<SourceParser> parsers, Serializers serializers) {
    this.parsers = parsers.stream().sorted(Comparator.comparing(SourceParser::priority)).toList();
    this.serializers = serializers;
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format) {
    for (SourceParser parser : parsers) {
      if (parser.accepts(format)) {
        return parser.parse(reader, format);
      }
    }
    return Either.left(null);
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Map<String, String> input, SourceFormat format) {
    return MapConfigParser.parse(input, format);
  }

  @Override
  public <T> Either<ConfigError, ConfigNode> parse(T value, ValueType<T> valueType) {
    return serializers.serialize(value, valueType);
  }
}
