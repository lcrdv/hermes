package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(IntegerDeserializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(IntegerDeserializer.FACTORY.supports(ValueType.from(Integer.class)));
    assertTrue(IntegerDeserializer.FACTORY.supports(ValueType.from(int.class)));
  }

  @Test
  void deserializesBoxed() {
    var result = deserializers.deserialize(ValueNode.of(123), ValueType.from(Integer.class));
    assertSuccess(123, result);
  }

  @Test
  void deserializesPrimitive() {
    var result = deserializers.deserialize(ValueNode.of(123), ValueType.from(int.class));
    assertSuccess(123, result);
  }

  @Test
  void deserializesFromShort() {
    var result = deserializers.deserialize(ValueNode.of((short) 7), ValueType.from(int.class));
    assertSuccess(7, result);
  }

  @Test
  void deserializesFromLong() {
    var result = deserializers.deserialize(ValueNode.of(9L), ValueType.from(int.class));
    assertSuccess(9, result);
  }

  @Test
  void deserializesNumericString() {
    var result = deserializers.deserialize(ValueNode.of("-13"), ValueType.from(int.class));
    assertSuccess(-13, result);
  }

  @Test
  void failsForNonNumericString() {
    var result = deserializers.deserialize(ValueNode.of("invalid lol"), ValueType.from(int.class));
    assertFailure(result);
  }

  @Test
  void failsAboveMax() {
    var result = deserializers.deserialize(ValueNode.of((long) Integer.MAX_VALUE + 1), ValueType.from(int.class));
    assertFailure(result);
  }

  @Test
  void failsBelowMin() {
    var result = deserializers.deserialize(ValueNode.of((long) Integer.MIN_VALUE - 1), ValueType.from(int.class));
    assertFailure(result);
  }
}
