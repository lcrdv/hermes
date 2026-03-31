package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(ShortSerializer.FACTORY)));

  @Test
  void supports() {
    assertTrue(ShortSerializer.FACTORY.supports(ValueType.from(Short.class)));
    assertTrue(ShortSerializer.FACTORY.supports(ValueType.from(short.class)));
  }

  @Test
  void serializesBoxedShort() {
    Short value = 123;

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(ValueNode.of(new ValueHolder.ShortHolder((short) 123)), result);
  }

  @Test
  void serializesPrimitiveShort() {
    short value = 123;

    var result = serializers.serialize(
        value,
        ValueType.from(short.class)
    );

    assertSuccess(ValueNode.of(new ValueHolder.ShortHolder((short) 123)), result);
  }
}
