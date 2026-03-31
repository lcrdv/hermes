package dev.lacrid.hermes.target;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

public interface ConfigWriters {
  Either<ConfigError, Void> write(ConfigNode node, Writer writer, WriterFormat format);
}
