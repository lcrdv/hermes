package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodeBuilder;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertSuccess;

class HierarchyDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(
      new HierarchyDeserializerFactory(), new RecordDeserializerFactory(),
      IntegerDeserializer.FACTORY, StringDeserializer.FACTORY))
  );


  @Test
  void simpleSealed() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("type", "some_weird_animal")
        .value("wtf", "xxxxz")
    );
    ValueType<Animal> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Animal.WeirdAnimal.SomeWeirdAnimal("xxxxz"),
        result
    );
  }

  sealed interface Animal {
    record Cat(String name, String meow) implements Animal {

    }

    record Dog(String name) implements Animal {

    }

    sealed interface WeirdAnimal extends Animal {
      record SomeWeirdAnimal(String wtf) implements WeirdAnimal {

      }

      record AnotherWeirdAnimal(int age) implements WeirdAnimal {

      }
    }
  }
}
