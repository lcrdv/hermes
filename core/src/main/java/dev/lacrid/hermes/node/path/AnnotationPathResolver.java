package dev.lacrid.hermes.node.path;

import dev.lacrid.hermes.annotations.ConfigPath;
import dev.lacrid.hermes.type.ValueType;

public class AnnotationPathResolver implements TypedPathResolver {
  private final PathResolver pathResolver;

  public AnnotationPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
  }

  @Override
  public NodePath resolve(String name, ValueType<?> type) {
    return type.annotations()
        .find(ConfigPath.class)
        .map(ConfigPath::value)
        .map(NodePath::of)
        .orElseGet(() -> pathResolver.resolve(name));
  }
}
