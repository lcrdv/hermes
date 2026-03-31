package dev.lacrid.hermes.target;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

public interface ConfigOutput {
  Either<ConfigError, Void> write(ConfigNode node, ConfigWriters writer);
}
