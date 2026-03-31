package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.annotations.ConfigPath;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodeBuilder;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.serialization.deserializer.provider.DefaultsDeserializerProvider;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.*;

class ProxyDeserializerTest {
  Deserializers deserializers = new Deserializers(new DefaultsDeserializerProvider(new FactoryDeserializerProvider(List.of(
      new ProxyDeserializerFactory(), StringDeserializer.FACTORY, IntegerDeserializer.FACTORY, new ListDeserializerFactory()))));

  @Test
  void simpleProxy() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "kotek")
        .list("owners", owners -> owners
            .value("raz")
            .value("dwa"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("name", "dwa"))
        )
    );
    ValueType<Cat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(result);
    assertEquals("kotek", result.getRight().name());
    assertEquals(List.of("raz", "dwa"), result.getRight().owners());
    assertFalse(result.getRight().meows().isEmpty());
    assertEquals("dwa", result.getRight().meows().getFirst().name());
  }

  @Test
  void inheritance() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("name", "kotek")
        .value("color", "black")
        .list("owners", owners -> owners
            .value("raz")
            .value("dwa"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("name", "dwa"))
        )
    );
    ValueType<ColoredCat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(result);
    assertEquals("kotek", result.getRight().name());
    assertEquals("black", result.getRight().color());
    assertEquals(List.of("raz", "dwa"), result.getRight().owners());
    assertFalse(result.getRight().meows().isEmpty());
    assertEquals("dwa", result.getRight().meows().getFirst().name());
  }

  @Test
  void defaultValueWithInheritance() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("color", "black")
        .value("super_power", "flying cat")
        .list("owners", owners -> owners
            .value("raz")
            .value("dwa"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("name", "dwa"))
        )
    );
    ValueType<NamedSuperCat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(result);
    assertEquals("such a beautiful name", result.getRight().name());
    assertEquals("flying cat", result.getRight().superPower());
    assertEquals(List.of("raz", "dwa"), result.getRight().owners());
    assertFalse(result.getRight().meows().isEmpty());
    assertEquals("dwa", result.getRight().meows().getFirst().name());
  }

  @Test
  void multipleDefaultValues() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("color", "black")
        .list("owners", owners -> owners
            .value("raz")
            .value("dwa"))
        .list("meows", meows -> meows
            .map(meow -> meow.value("name", "dwa"))
        )
    );
    ValueType<SuperiorCat> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(result);
    assertEquals("superior cat", result.getRight().name());
    assertEquals("best cat", result.getRight().superPower());
    assertEquals(List.of("raz", "dwa"), result.getRight().owners());
    assertFalse(result.getRight().meows().isEmpty());
    assertEquals("dwa", result.getRight().meows().getFirst().name());
  }

  @Test
  void customPath() {
    ConfigNode node = NodeBuilder.map(builder -> builder
        .value("host", "1337")
        .value("port123", 6379)
        .map("login", login -> login.value("password", "12345"))
    );
    ValueType<Database> type = ValueType.from(new TypeReference<>() {});

    var result = deserializers.deserialize(node, type);

    assertSuccess(result);
    assertEquals("1337", result.getRight().host());
    assertEquals(6379, result.getRight().port());
    assertEquals("root", result.getRight().user());
    assertEquals("12345", result.getRight().password());
  }

  public interface Cat {
    String name();

    List<String> owners();

    List<Meow> meows();
  }

  public interface Meow {
    String name();
  }

  public interface ColoredCat extends Cat {
    String color();
  }

  public interface NamedSuperCat extends Cat {
    @Override
    default String name() {
      return "such a beautiful name";
    }

    String superPower();
  }

  public interface SuperiorCat extends NamedSuperCat, ColoredCat {
    @Override
    default String name() {
      return "superior cat";
    }

    default String superPower() {
      return "best cat";
    }
  }

  public interface Database {
    String host();

    @ConfigPath("port123")
    int port();

    @ConfigPath({"login", "user"})
    default String user() {
      return "root";
    }

    @ConfigPath({"login", "password"})
    String password();
  }
}
