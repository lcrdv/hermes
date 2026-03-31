package dev.lacrid.hermes.error;

import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;

import java.util.List;

public record LocalizedError(NodePath path, ConfigError error) {
  public static List<ConfigError> flatten(List<LocalizedError> errors) {
    return errors.stream()
        .<ConfigError>map(error -> new ConfigError.KeyedError(
            String.join(".", error.path.keys().stream()
                .map(NodeKey::key).toList()),
            error.error))
        .toList();
  }
}
