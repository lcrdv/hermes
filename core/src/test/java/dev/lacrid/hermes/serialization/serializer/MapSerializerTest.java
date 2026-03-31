package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static dev.lacrid.hermes.node.NodeBuilder.map;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;

class MapSerializerTest {
  Serializers serializers = new Serializers(FactorySerializerProvider.defaults());

  @Test
  void stringStringMap() {
    Map<String, String> value = Map.of("foo", "bar", "baz", "quix");

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(map().value("foo", "bar").value("baz", "quix"), result);
  }

  @Test
  void intIntMap() {
    Map<Integer, Integer> value = Map.of(53212, 123, 12345, 1337);

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(map().value("53212", 123).value("12345", 1337), result);
  }

  @Test
  void enumMap() {
    Map<TestEnum, Integer> value = Map.of(TestEnum.KEY_1, 123, TestEnum.KEY_2, 1337);

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(map().value("key_1", 123).value("key_2", 1337), result);
  }

  public enum TestEnum {
    KEY_1,
    KEY_2,
    KEY_3,
  }
}