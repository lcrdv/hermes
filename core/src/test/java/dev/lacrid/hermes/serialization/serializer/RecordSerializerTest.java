package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.serialization.serializer.provider.FactorySerializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.node.NodeBuilder.list;
import static dev.lacrid.hermes.node.NodeBuilder.map;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;

class RecordSerializerTest {
  Serializers serializers = new Serializers(FactorySerializerProvider.defaults());

  @Test
  void complexRecord() {
    Cat value = new Cat("meow",
        List.of("a", "b", "C"),
        List.of(new Meow("1sad"), new Meow("xdq12"))
    );

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
        )
        .add("meows", list()
            .add(map().value("sound", "1sad"))
            .add(map().value("sound", "xdq12"))
        ), result);
  }

  private record Cat(String name, List<String> owners, List<Meow> meows) {

  }

  private record Meow(String sound) {

  }
}