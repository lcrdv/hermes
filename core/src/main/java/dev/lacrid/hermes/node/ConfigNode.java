package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.metadata.Metadata;

public sealed interface ConfigNode permits ListNode, MapNode, ValueNode, NullNode {
  Metadata metadata();

  ConfigNode deepCopy();
}
