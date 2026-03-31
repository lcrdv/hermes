package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.serialization.deserializer.provider.FactoryDeserializerProvider;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static dev.lacrid.hermes.ResultAssertions.assertFailure;
import static dev.lacrid.hermes.ResultAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumDeserializerTest {
  Deserializers caseInsensitive = new Deserializers(new FactoryDeserializerProvider(List.of(new EnumDeserializerFactory())));
  Deserializers caseSensitive = new Deserializers(new FactoryDeserializerProvider(List.of(new EnumDeserializerFactory())),
      SerializationConfig.configure(config -> config.setIgnoreEnumCase(false)));

  @Test
  void supports() {
    ValueType<InvalidEnum> enumType = ValueType.from(InvalidEnum.class);
    EnumDeserializerFactory enumDeserializerFactory = new EnumDeserializerFactory();

    assertTrue(enumDeserializerFactory.supports(enumType));
  }

  @Test
  void matchingCase() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);

    for (TestEnum enumValue : TestEnum.values()) {
      ConfigNode node = ValueNode.of(enumValue.name());

      var result = caseInsensitive.deserialize(node, enumType);

      assertSuccess(enumValue, result);
    }
  }

  @Test
  void upperCase() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);

    for (TestEnum enumValue : TestEnum.values()) {
      ConfigNode node = ValueNode.of(enumValue.name().toUpperCase());

      var result = caseInsensitive.deserialize(node, enumType);

      assertSuccess(enumValue, result);
    }
  }

  @Test
  void lowerCase() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);

    for (TestEnum enumValue : TestEnum.values()) {
      ConfigNode node = ValueNode.of(enumValue.name().toLowerCase());

      var result = caseInsensitive.deserialize(node, enumType);

      assertSuccess(enumValue, result);
    }
  }

  @Test
  void randomizedCase() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);
    List<Pair<ConfigNode, TestEnum>> input = Arrays.asList(
        new Pair<>(ValueNode.of("threE"), TestEnum.Three),
        new Pair<>(ValueNode.of("tHrEE"), TestEnum.Three),
        new Pair<>(ValueNode.of("oNe"), TestEnum.ONE),
        new Pair<>(ValueNode.of("OnE"), TestEnum.ONE),
        new Pair<>(ValueNode.of("four"), TestEnum.four)
    );

    input.forEach(values -> {
      var result = caseInsensitive.deserialize(values.first(), enumType);

      assertSuccess(values.second(), result);
    });
  }

  @Test
  void failsWithInvalidValues() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);
    List<ConfigNode> input = Arrays.asList(
        ValueNode.of("onee"),
        ValueNode.of("invalid lol")
    );

    input.forEach(value -> {
      var result = caseInsensitive.deserialize(value, enumType);

      assertFailure(result);
    });
  }

  @Test
  void failsWithInvalidEnum() {
    ValueType<InvalidEnum> enumType = ValueType.from(InvalidEnum.class);
    ConfigNode node = ValueNode.of("one");

    var result = caseInsensitive.deserialize(node, enumType);

    assertFailure(result);
  }

  @Test
  void caseSensitive() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);
    List<Pair<ConfigNode, TestEnum>> input = Arrays.asList(
        new Pair<>(ValueNode.of("ONE"), TestEnum.ONE),
        new Pair<>(ValueNode.of("tWo"), TestEnum.tWo),
        new Pair<>(ValueNode.of("Three"), TestEnum.Three),
        new Pair<>(ValueNode.of("four"), TestEnum.four),
        new Pair<>(ValueNode.of("fiVE"), TestEnum.fiVE)
    );

    input.forEach(values -> {
      var result = caseSensitive.deserialize(values.first(), enumType);

      assertSuccess(values.second(), result);
    });
  }

  @Test
  void caseSensitiveFailsWithInvalidCase() {
    ValueType<TestEnum> enumType = ValueType.from(TestEnum.class);
    List<ConfigNode> input = Arrays.asList(
        ValueNode.of("ONe"),
        ValueNode.of("TWO"),
        ValueNode.of("three"),
        ValueNode.of("FOUR"),
        ValueNode.of("FIVE")
    );

    input.forEach(value -> {
      var result = caseSensitive.deserialize(value, enumType);

      assertFailure(result);
    });
  }

  enum TestEnum {
    ONE,
    tWo,
    Three,
    four,
    fiVE,
  }

  enum InvalidEnum {
    ONE,
    one
  }
}