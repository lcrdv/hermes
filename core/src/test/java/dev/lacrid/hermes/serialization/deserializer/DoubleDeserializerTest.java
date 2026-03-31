package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(DoubleDeserializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(DoubleDeserializer.FACTORY.supports(ValueType.from(Double.class)));
    assertTrue(DoubleDeserializer.FACTORY.supports(ValueType.from(double.class)));
  }

  @Test
  void deserializesBoxed() {
    var result = deserializers.deserialize(ValueNode.of(123.0), ValueType.from(Double.class));
    assertSuccess(123.0, result);
  }

  @Test
  void deserializesPrimitive() {
    var result = deserializers.deserialize(ValueNode.of(123.0), ValueType.from(double.class));
    assertSuccess(123.0, result);
  }

  @Test
  void deserializesFromShort() {
    var result = deserializers.deserialize(ValueNode.of((short) 7), ValueType.from(double.class));
    assertSuccess(7.0, result);
  }

  @Test
  void deserializesFromLong() {
    var result = deserializers.deserialize(ValueNode.of(9L), ValueType.from(double.class));
    assertSuccess(9.0, result);
  }

  @Test
  void deserializesNumericString() {
    var result = deserializers.deserialize(ValueNode.of("-13.5"), ValueType.from(double.class));
    assertSuccess(-13.5, result);
  }

  @Test
  void failsForNonNumericString() {
    var result = deserializers.deserialize(ValueNode.of("invalid lol"), ValueType.from(double.class));
    assertFailure(result);
  }
}
