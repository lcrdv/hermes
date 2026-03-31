package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ListNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.ValueHolder;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GsonParserTest {
  @Test
  void parsesSimpleSource() {
    String json = """
        {
          "plain": "text",
          "flag": true,
          "nested.path": {
            "answer": 42
          },
          "items": [1, null, {"enabled": false}]
        }
        """;

    var result = new GsonParser().parse(
        new StringReader(json),
        new SourceFormat("json", name -> NodePath.of(Arrays.asList(name.split("\\."))))
    );

    assertTrue(result.isRight(), () -> String.valueOf(result.getLeft()));

    MapNode root = assertInstanceOf(MapNode.class, result.getRight());
    assertEquals(new ValueHolder.StringHolder("text"), value(root, "plain").holder());
    assertEquals(new ValueHolder.BooleanHolder(true), value(root, "flag").holder());

    MapNode nested = assertInstanceOf(MapNode.class, node(root, "nested"));
    MapNode path = assertInstanceOf(MapNode.class, node(nested, "path"));
    assertEquals(new ValueHolder.StringHolder("42"), value(path, "answer").holder());

    ListNode items = assertInstanceOf(ListNode.class, node(root, "items"));
    assertEquals(3, items.elements().size());
    assertEquals(new ValueHolder.StringHolder("1"), assertInstanceOf(ValueNode.class, items.elements().get(0)).holder());
    assertEquals(new ValueHolder.NullHolder(), assertInstanceOf(ValueNode.class, items.elements().get(1)).holder());

    MapNode listObject = assertInstanceOf(MapNode.class, items.elements().get(2));
    assertEquals(new ValueHolder.BooleanHolder(false), value(listObject, "enabled").holder());
  }

  private static ConfigNode node(MapNode mapNode, String key) {
    return mapNode.findExact(NodeKey.of(key)).orElseThrow();
  }

  private static ValueNode value(MapNode mapNode, String key) {
    return assertInstanceOf(ValueNode.class, node(mapNode, key));
  }
}
