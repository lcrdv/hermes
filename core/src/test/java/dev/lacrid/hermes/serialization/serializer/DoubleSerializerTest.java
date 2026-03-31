package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoubleSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(DoubleSerializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(DoubleSerializer.FACTORY.supports(ValueType.from(Double.class)));
    assertTrue(DoubleSerializer.FACTORY.supports(ValueType.from(double.class)));
  }

  @Test
  void serializesBoxedDouble() {
    Double value = 123.0;

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(ValueNode.of(123.0), result);
  }

  @Test
  void serializesPrimitiveDouble() {
    double value = 123.0;

    var result = serializers.serialize(
        value,
        ValueType.from(double.class)
    );

    assertSuccess(ValueNode.of(123.0), result);
  }
}
