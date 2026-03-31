package dev.lacrid.hermes.target;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.io.Writer;
import java.util.Comparator;
import java.util.List;

public final class DefaultConfigWriters implements ConfigWriters {
  private final List<ConfigWriter> writers;

  public DefaultConfigWriters(List<ConfigWriter> writers) {
    this.writers = writers.stream().sorted(Comparator.comparing(ConfigWriter::priority)).toList();
  }

  @Override
  public Either<ConfigError, Void> write(ConfigNode node, Writer writer, WriterFormat format) {
    for (ConfigWriter configWriter : writers) {
      if (configWriter.accepts(format)) {
        return configWriter.write(node, writer, format);
      }
    }
    return Either.left(null);
  }
}
