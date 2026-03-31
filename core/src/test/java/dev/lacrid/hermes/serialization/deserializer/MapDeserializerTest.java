package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MapDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(new MapDeserializerFactory(), StringDeserializer.FACTORY, IntegerDeserializer.FACTORY, new EnumDeserializerFactory())));

  @Test
  void stringStringMap() {
    ConfigNode node = MapNode.of(Map.of(
        NodeKey.of("foo"), ValueNode.of("bar"),
        NodeKey.of("baz"), ValueNode.of("quix")
    ));
    ValueType<Map<String, String>> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(Map.of("foo", "bar", "baz", "quix"), result);
  }

  @Test
  void stringIntMap() {
    ConfigNode node = MapNode.of(Map.of(
        NodeKey.of("foo"), ValueNode.of(123),
        NodeKey.of("baz"), ValueNode.of("1337")
    ));
    ValueType<Map<String, Integer>> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(Map.of("foo", 123, "baz", 1337), result);
  }

  @Test
  void intIntMap() {
    ConfigNode node = MapNode.of(Map.of(
        NodeKey.of("53212"), ValueNode.of(123),
        NodeKey.of("12345"), ValueNode.of("1337")
    ));
    ValueType<Map<Integer, Integer>> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(Map.of(53212, 123, 12345, 1337), result);
  }

  @Test
  void enumMap() {
    ConfigNode node = MapNode.of(Map.of(
        NodeKey.of("key_1"), ValueNode.of(123),
        NodeKey.of("KEY_2"), ValueNode.of("1337")
    ));
    ValueType<EnumMap<TestEnum, Integer>> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(new EnumMap<>(Map.of(
        TestEnum.KEY_1, 123,
        TestEnum.KEY_2, 1337)
    ), result);
    assertInstanceOf(EnumMap.class, result.getRight());
  }

  public enum TestEnum {
    KEY_1,
    KEY_2,
    KEY_3,
  }
}