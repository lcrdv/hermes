package dev.lacrid.hermes.node;

import org.junit.jupiter.api.Assertions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeAssertions {
  private NodeAssertions() {
  }

  public static void assertContentEquals(ConfigNode expected, ConfigNode result) {
    Assertions.assertInstanceOf(expected.getClass(), result);
    switch (expected) {
      case ListNode expectedNode -> {
        ListNode resultNode = (ListNode) result;

        assertEquals(expectedNode.elements().size(), resultNode.elements().size());

        for (int i = 0; i < expectedNode.elements().size(); i++) {
          ConfigNode expectedElement = expectedNode.elements().get(i);
          ConfigNode resultElement = resultNode.elements().get(i);
          assertContentEquals(expectedElement, resultElement);
        }
      }
      case MapNode expectedNode -> {
        MapNode resultNode = (MapNode) result;

        assertEquals(expectedNode.keys(), resultNode.keys());

        for (MapNode.Entry entry : expectedNode.entries()) {
          Optional<ConfigNode> resultEntry = resultNode.findExact(entry.key());
          assertTrue(resultEntry.isPresent());
          assertContentEquals(entry.node(), resultEntry.get());
        }
      }
      case ValueNode expectedNode -> {
        ValueNode resultNode = (ValueNode) result;
        assertEquals(expectedNode.holder(), resultNode.holder());
      }
      case NullNode expectedNode -> {}
    }
  }
}
