package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LongSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(LongSerializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(LongSerializer.FACTORY.supports(ValueType.from(Long.class)));
    assertTrue(LongSerializer.FACTORY.supports(ValueType.from(long.class)));
  }

  @Test
  void serializesBoxedLong() {
    Long value = 123L;

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(ValueNode.of(123L), result);
  }

  @Test
  void serializesPrimitiveLong() {
    long value = 123L;

    var result = serializers.serialize(
        value,
        ValueType.from(long.class)
    );

    assertSuccess(ValueNode.of(123L), result);
  }
}
