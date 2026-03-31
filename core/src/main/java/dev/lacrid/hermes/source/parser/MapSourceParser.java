package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.util.Collection;
import java.util.Map;

public abstract class MapSourceParser extends BaseSourceParser {
  protected MapSourceParser(Collection<String> acceptedFormats) {
    super(acceptedFormats);
  }

  protected Either<ConfigError, ConfigNode> parse(Map<String, String> input, SourceFormat format) {
    return MapConfigParser.parse(input, format);
  }
}
