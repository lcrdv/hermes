package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static dev.lacrid.hermes.node.NodeBuilder.list;
import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SetSerializerTest {
  Serializers serializers = new Serializers(new FactorySerializerProvider(List.of(new SetSerializerFactory(), StringSerializer.FACTORY, new MapSerializerFactory(), IntegerSerializer.FACTORY)));

  @Test
  void supports() {
    SetSerializerFactory factory = new SetSerializerFactory();

    assertTrue(factory.supports(ValueType.from(Set.class)));
    assertTrue(factory.supports(ValueType.from(LinkedHashSet.class)));
  }

  @Test
  void simpleSet() {
    Set<String> value = new LinkedHashSet<>(List.of("a", "b", "C"));

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(list().value("a").value("b").value("C"), result);
  }

  @Test
  void setOfMaps() {
    Set<Map<String, Integer>> value = new LinkedHashSet<>(List.of(
        Map.of("foo", 1, "bar", 2),
        Map.of("baz", 3)
    ));

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
  void emptySet() {
    Set<String> value = Set.of();

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(list(), result);
  }

  @Test
  void missingParameterFails() {
    Set<String> value = Set.of();

    var result = serializers.serialize(
        value,
        ValueType.from(Set.class)
    );

    assertFailure(ConfigError.UndefinedParameterType.class, result);
  }

  @Test
  void unsupportedElementTypeFails() {
    Set<Float> value = Set.of();

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertFailure(ConfigError.UnknownSerializer.class, result);
  }
}
