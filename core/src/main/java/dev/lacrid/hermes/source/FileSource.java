package dev.lacrid.hermes.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileSource extends ConfigurableSource<FileSource> {
  private final Path path;
  private final boolean isOptional;

  public FileSource(Path path, boolean isOptional) {
    this.path = path;
    this.isOptional = isOptional;
  }

  private FileSource(Path path) {
    this(path, false);
  }

  @Override
  protected FileSource self() {
    return this;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    if (!Files.exists(path)) {
      return isOptional ? Either.right(NullNode.create()) : Either.left(new ConfigError.UnknownFile(path));
    }

    try (Reader reader = Files.newBufferedReader(path)) {
      return parser.parse(reader, format());
    } catch (IOException exception) {
      return Either.left(new ConfigError.SourceLoadingError(path.toString()));
    }
  }

  public static FileSource of(String path) {
    return new FileSource(Path.of(path));
  }

  public static FileSource of(Path path) {
    return new FileSource(path);
  }
}
