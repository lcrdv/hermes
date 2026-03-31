package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class PropertiesParser extends MapSourceParser {
  public PropertiesParser() {
    super(List.of("properties"));
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format) {
    Properties properties = new Properties();
    try {
      properties.load(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Map<String, String> valuesMap = properties.entrySet().stream().collect(
        Collectors.toMap(
            entry -> entry.getKey().toString(),
            entry -> entry.getValue().toString()
        )
    );

    return parse(valuesMap, format);
  }
}
