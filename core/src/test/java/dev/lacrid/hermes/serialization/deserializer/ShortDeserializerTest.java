package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(ShortDeserializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(ShortDeserializer.FACTORY.supports(ValueType.from(Short.class)));
    assertTrue(ShortDeserializer.FACTORY.supports(ValueType.from(short.class)));
  }

  @Test
  void deserializesBoxed() {
    var result = deserializers.deserialize(ValueNode.of((short) 123), ValueType.from(Short.class));
    assertSuccess((short) 123, result);
  }

  @Test
  void deserializesPrimitive() {
    var result = deserializers.deserialize(ValueNode.of((short) 123), ValueType.from(short.class));
    assertSuccess((short) 123, result);
  }

  @Test
  void deserializesFromShort() {
    var result = deserializers.deserialize(ValueNode.of((short) 7), ValueType.from(short.class));
    assertSuccess((short) 7, result);
  }

  @Test
  void deserializesLongInRange() {
    var result = deserializers.deserialize(ValueNode.of(9L), ValueType.from(short.class));
    assertSuccess((short) 9, result);
  }

  @Test
  void deserializesNumericString() {
    var result = deserializers.deserialize(ValueNode.of("-13"), ValueType.from(short.class));
    assertSuccess((short) -13, result);
  }

  @Test
  void failsForNonNumericString() {
    var result = deserializers.deserialize(ValueNode.of("invalid lol"), ValueType.from(short.class));
    assertFailure(result);
  }

  @Test
  void failsAboveMax() {
    var result = deserializers.deserialize(ValueNode.of((int) Short.MAX_VALUE + 1), ValueType.from(short.class));
    assertFailure(result);
  }

  @Test
  void failsBelowMin() {
    var result = deserializers.deserialize(ValueNode.of((int) Short.MIN_VALUE - 1), ValueType.from(short.class));
    assertFailure(result);
  }
}
