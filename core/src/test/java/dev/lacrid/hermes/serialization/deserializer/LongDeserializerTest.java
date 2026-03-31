package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(LongDeserializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(LongDeserializer.FACTORY.supports(ValueType.from(Long.class)));
    assertTrue(LongDeserializer.FACTORY.supports(ValueType.from(long.class)));
  }

  @Test
  void deserializesBoxed() {
    var result = deserializers.deserialize(ValueNode.of(123L), ValueType.from(Long.class));
    assertSuccess(123L, result);
  }

  @Test
  void deserializesPrimitive() {
    var result = deserializers.deserialize(ValueNode.of(123L), ValueType.from(long.class));
    assertSuccess(123L, result);
  }

  @Test
  void deserializesFromShort() {
    var result = deserializers.deserialize(ValueNode.of((short) 7), ValueType.from(long.class));
    assertSuccess(7L, result);
  }

  @Test
  void deserializesFromInt() {
    var result = deserializers.deserialize(ValueNode.of(9), ValueType.from(long.class));
    assertSuccess(9L, result);
  }

  @Test
  void deserializesNumericString() {
    var result = deserializers.deserialize(ValueNode.of("-13"), ValueType.from(long.class));
    assertSuccess(-13L, result);
  }

  @Test
  void failsForNonNumericString() {
    var result = deserializers.deserialize(ValueNode.of("invalid lol"), ValueType.from(long.class));
    assertFailure(result);
  }
}
