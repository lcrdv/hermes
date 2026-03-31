package dev.lacrid.hermes.target;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.parser.SourceFormat;
import dev.lacrid.hermes.util.Priority;
import dev.lacrid.lambda.Either;

import java.io.OutputStream;
import java.io.Writer;

public interface ConfigWriter {
  Either<ConfigError, Void> write(ConfigNode node, Writer writer, WriterFormat format);

  boolean accepts(WriterFormat format);

  default Priority priority() {
    return Priority.NORMAL;
  }
}
