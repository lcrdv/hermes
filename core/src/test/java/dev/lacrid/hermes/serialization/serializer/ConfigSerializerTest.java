package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.annotations.Config;
import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.node.NodeBuilder.list;
import static dev.lacrid.hermes.node.NodeBuilder.map;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;

class ConfigSerializerTest {
  Serializers serializers = new Serializers(FactorySerializerProvider.defaults());

  @Test
  void simpleConfig() {
    Cat value = new Cat("meow", List.of("a", "b", "C"));

    var result = serializers.serialize(
        value,
        ValueType.from(new TypeReference<>() {})
    );

    assertSuccess(map()
        .value("name", "meow")
        .add("owners", list()
            .value("a")
            .value("b")
            .value("C")
        ), result);
  }

  @Config
  public static class Cat {
    private final String name;
    private final List<String> owners;

    public Cat(String name, List<String> owners) {
      this.name = name;
      this.owners = owners;
    }
  }
}