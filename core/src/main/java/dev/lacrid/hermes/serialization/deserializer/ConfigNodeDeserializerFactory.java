package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public final class ConfigNodeDeserializerFactory implements DeserializerFactory<ConfigNode> {
  @Override
  public Either<ConfigError, Deserializer<ConfigNode>> make(ValueType<ConfigNode> type, DeserializerContext context) {
    return Either.right(new ConfigNodeDeserializer());
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.clazz() == ConfigNode.class;
  }

  private static class ConfigNodeDeserializer implements Deserializer<ConfigNode> {
    @Override
    public Either<ConfigError, ConfigNode> deserialize(ConfigNode node, ConfigNode defaultValue) {
      return Either.right(node.deepCopy());
    }
  }
}
