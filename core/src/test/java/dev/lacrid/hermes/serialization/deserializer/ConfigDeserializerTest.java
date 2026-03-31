package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.annotations.Config;
import dev.lacrid.hermes.annotations.Named;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodeBuilder;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigDeserializerTest {
  Deserializers deserializers = new Deserializers(new FactoryDeserializerProvider(List.of(
      new ConfigDeserializerFactory(), StringDeserializer.FACTORY,
      IntegerDeserializer.FACTORY, new ListDeserializerFactory()))
  );

  @Test
  void supports() {
    ValueType<Cat> correctType = ValueType.from(new TypeReference<>() {});
    ValueType<NotAConfig> incorrectType = ValueType.from(new TypeReference<>() {});
    ConfigDeserializerFactory deserializer = new ConfigDeserializerFactory();

    assertTrue(deserializer.supports(correctType));
    assertFalse(deserializer.supports(incorrectType));
  }

  @Test
  void simpleConfig() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("sound", "meow")
        .value("volume", 10)
    );
    ValueType<Meow> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Meow("meow", 10),
        result
    );
  }

  @Test
  void complexConfig() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "cat")
        .list("owners", owners -> owners
            .value("owner1")
            .value("owner2"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("sound", "meow").value("volume", "5"))
            .map(meow -> meow.value("sound", "purr").value("volume", "10"))
        )
    );
    ValueType<Cat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new Cat("cat",
            List.of("owner1", "owner2"),
            List.of(new Meow("meow", 5), new Meow("purr", 10))),
        result
    );
  }

  @Test
  void complexConfigUsingConstructor() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "cat")
        .list("owners", owners -> owners
            .value("owner1")
            .value("owner2"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("sound", "meow").value("volume", "5"))
            .map(meow -> meow.value("sound", "purr").value("volume", "10"))
        )
    );
    ValueType<ConstructorCat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(
        new ConstructorCat("cat",
            List.of("owner1", "owner2"),
            List.of(new Meow("meow", 5), new Meow("purr", 10))),
        result
    );
  }

  @Config
  public static class Cat {
    private String name;
    private List<String> owners;
    private List<Meow> meows;

    public Cat(String name, List<String> owners, List<Meow> meows) {
      this.name = name;
      this.owners = owners;
      this.meows = meows;
    }

    public Cat() {
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Cat cat = (Cat) o;
      return Objects.equals(name, cat.name) && Objects.equals(owners, cat.owners) && Objects.equals(meows, cat.meows);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, owners, meows);
    }
  }

  @Config
  public static class Meow {
    private String sound;
    private int volume;

    public Meow(String sound, int volume) {
      this.sound = sound;
      this.volume = volume;
    }

    public Meow() {
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      Meow meow = (Meow) o;
      return volume == meow.volume && Objects.equals(sound, meow.sound);
    }

    @Override
    public int hashCode() {
      return Objects.hash(sound, volume);
    }
  }

  public static class ConstructorCat {
    private String name;
    private List<String> owners;
    private List<Meow> meows;

    @Config
    public ConstructorCat(@Named("name") String name, @Named("owners") List<String> owners, @Named("meows") List<Meow> meows) {
      this.name = name;
      this.owners = owners;
      this.meows = meows;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      ConstructorCat that = (ConstructorCat) o;
      return Objects.equals(name, that.name) && Objects.equals(owners, that.owners) && Objects.equals(meows, that.meows);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name, owners, meows);
    }
  }

  public static class NotAConfig {
    private String abc;
  }
}