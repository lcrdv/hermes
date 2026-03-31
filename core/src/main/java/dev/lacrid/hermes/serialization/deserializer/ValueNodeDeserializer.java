package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ValueNode;

public abstract class ValueNodeDeserializer<T> extends SpecificNodeDeserializer<ValueNode, T> {
  public ValueNodeDeserializer() {
    super(ValueNode.class);
  }
}
