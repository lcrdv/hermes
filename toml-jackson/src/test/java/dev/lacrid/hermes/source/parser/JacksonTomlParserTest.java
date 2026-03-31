package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.naming.Lexer;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ListNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.PathResolver;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonTomlParserTest {
  @Test
  void parsesSimpleSource() {
    String toml = """
        plain = "text"
        flag = true
        answer = 42
        precise = 1234567890.1234567890123456789
        items = [1, "two", false]

        [nested.path]
        enabled = true
        """;

    var result = new JacksonTomlParser().parse(
        new StringReader(toml),
        new SourceFormat("toml", PathResolver.separateKeys(Lexer.dotSeparated()))
    );

    assertTrue(result.isRight(), () -> String.valueOf(result.getLeft()));

    MapNode root = assertInstanceOf(MapNode.class, result.getRight());
    assertEquals(new ValueHolder.StringHolder("text"), value(root, "plain").holder());
    assertEquals(new ValueHolder.BooleanHolder(true), value(root, "flag").holder());
    assertEquals(new ValueHolder.IntegerHolder(42), value(root, "answer").holder());
    assertEquals(
        new ValueHolder.StringHolder("1234567890.1234567890123456789"),
        value(root, "precise").holder()
    );

    ListNode items = assertInstanceOf(ListNode.class, node(root, "items"));
    assertEquals(3, items.elements().size());
    assertEquals(new ValueHolder.IntegerHolder(1), assertInstanceOf(ValueNode.class, items.elements().get(0)).holder());
    assertEquals(new ValueHolder.StringHolder("two"), assertInstanceOf(ValueNode.class, items.elements().get(1)).holder());
    assertEquals(new ValueHolder.BooleanHolder(false), assertInstanceOf(ValueNode.class, items.elements().get(2)).holder());

    MapNode nested = assertInstanceOf(MapNode.class, node(root, "nested"));
    MapNode path = assertInstanceOf(MapNode.class, node(nested, "path"));
    assertEquals(new ValueHolder.BooleanHolder(true), value(path, "enabled").holder());
  }

  private static ConfigNode node(MapNode mapNode, String key) {
    return mapNode.findExact(NodeKey.of(key)).orElseThrow();
  }

  private static ValueNode value(MapNode mapNode, String key) {
    return assertInstanceOf(ValueNode.class, node(mapNode, key));
  }
}
