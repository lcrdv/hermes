package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.util.Priority;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.io.InputStream;
import java.io.Reader;

public interface SourceParser {
  Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format);

  boolean accepts(SourceFormat format);

  default Priority priority() {
    return Priority.NORMAL;
  }
}
