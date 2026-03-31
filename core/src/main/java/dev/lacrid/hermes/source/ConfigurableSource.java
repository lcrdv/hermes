package dev.lacrid.hermes.source;

import dev.lacrid.hermes.naming.Lexer;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.hermes.source.parser.SourceFormat;

public abstract class ConfigurableSource<T extends ConfigurableSource<T>> implements ConfigSource {
  private String extension = null;
  private PathResolver pathResolver = null;

  public T setExtension(String extension) {
    this.extension = extension;
    return self();
  }

  public T setPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
    return self();
  }

  protected abstract T self();

  protected SourceFormat format() {
    String extension = this.extension != null ? this.extension : baseExtension();
    PathResolver pathResolver = this.pathResolver != null ? this.pathResolver : basePathResolver();
    return new SourceFormat(extension, pathResolver);
  }

  protected String baseExtension() {
    return "";
  }

  protected PathResolver basePathResolver() {
    return PathResolver.separateKeys(Lexer.dotSeparated());
  }
}
