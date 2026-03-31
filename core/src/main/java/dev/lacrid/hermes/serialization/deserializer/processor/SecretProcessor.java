package dev.lacrid.hermes.serialization.deserializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

public class SecretProcessor implements NodeProcessor {
  @Override
  public Either<ConfigError, ConfigNode> handle(ConfigNode node) {
    return null;
  }
}
