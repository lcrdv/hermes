package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(IntegerSerializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(IntegerSerializer.FACTORY.supports(ValueType.from(Integer.class)));
    assertTrue(IntegerSerializer.FACTORY.supports(ValueType.from(int.class)));
  }

  @Test
  void serializesBoxedInt() {
    Integer value = 123;

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(ValueNode.of(123), result);
  }

  @Test
  void serializesPrimitiveInt() {
    int value = 123;

    var result = serializers.serialize(
        value,
        ValueType.from(int.class)
    );

    assertSuccess(ValueNode.of(123), result);
  }
}
