package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.serialization.deserializer.provider.DefaultsDeserializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static dev.lacrid.hermes.ResultAssertions.assertSuccess;

class RecordDeserializerTest {
  Deserializers deserializers = new Deserializers(new DefaultsDeserializerProvider(new FactoryDeserializerProvider(List.of(
      new RecordDeserializerFactory(), StringDeserializer.FACTORY, InstantDeserializer.FACTORY,
      DoubleDeserializer.FACTORY, new ListDeserializerFactory()))));

  @Test
  void simpleRecord() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("sound", "meow")
    );
    ValueType<Meow> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Meow("meow"),
        result
    );
  }

  @Test
  void complexRecord() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "cat")
        .list("owners", owners -> owners
            .value("owner1")
            .value("owner2"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("sound", "meow"))
            .map(meow -> meow.value("sound", "purr"))
        )
    );
    ValueType<Cat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Cat("cat",
            List.of("owner1", "owner2"),
            List.of(new Meow("meow"), new Meow("purr"))),
        result
    );
  }

  @Test
  void usesDefaultValues() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "bettercat")
    );
    ValueType<Product> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Product("bettercat", Instant.MIN, Collections.emptyList(), 12.2),
        result
    );
  }

  public record Product(String name, Instant createdAt, List<Entry> entries, double age) {
    public Product() {
      this("cat", Instant.MIN, Collections.emptyList(), 12.2);
    }
  }

  public record Entry(String id, Instant updatedAt) {

  }

  public record Cat(String name, List<String> owners, List<Meow> meows) {

  }

  public record Meow(String sound) {

  }
}