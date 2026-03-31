package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;

class OptionalSerializerTest {
  Serializers serializers = new Serializers(FactorySerializerProvider.defaults());

  @Test
  void presentString() {
    Optional<String> value = Optional.of("value");

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(ValueNode.of("value"), result);
  }

  @Test
  void emptyString() {
    Optional<String> value = Optional.empty();

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(NullNode.create(), result);
  }
}