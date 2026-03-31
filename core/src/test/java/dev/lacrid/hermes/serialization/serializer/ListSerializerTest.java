package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.lacrid.hermes.node.NodeBuilder.list;
import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(
      new ListSerializerFactory(), StringSerializer.FACTORY, new MapSerializerFactory(), IntegerSerializer.FACTORY))
  );

  @Test
  void supports() {
    ListSerializerFactory factory = new ListSerializerFactory();

    assertTrue(factory.supports(ValueType.from(List.class)));
    assertTrue(factory.supports(ValueType.from(ArrayList.class)));
  }

  @Test
  void simpleList() {
    List<String> value = List.of("a", "b", "C");

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(list().value("a").value("b").value("C"), result);
  }

  @Test
  void listOfMaps() {
    List<Map<String, Integer>> value = List.of(
        Map.of("foo", 1, "bar", 2),
        Map.of("baz", 3)
    );

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(
        list()
            .map(node -> node.value("foo", 1).value("bar", 2))
            .map(node -> node.value("baz", 3)),
        result
    );
  }

  @Test
  void emptyList() {
    List<String> value = List.of();

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(list(), result);
  }

  @Test
  void missingParameterFails() {
    List<String> value = List.of();

    var result = serializers.serialize(
        value,
        ValueType.from(List.class)
    );

    assertFailure(ConfigError.UndefinedParameterType.class, result);
  }

  @Test
  void unsupportedElementTypeFails() {
    List<Float> value = List.of();

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertFailure(ConfigError.UnknownSerializer.class, result);
  }
}
