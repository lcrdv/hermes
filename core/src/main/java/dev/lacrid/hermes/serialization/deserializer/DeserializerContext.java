package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.type.ValueType;

public record DeserializerContext(Deserializers deserializers, SerializationConfig config) {

  public NodeErrors errorFactory(ValueType<?> type) {
    return new NodeErrors(config.nodePrinter(), type.clazz());
  }
}
