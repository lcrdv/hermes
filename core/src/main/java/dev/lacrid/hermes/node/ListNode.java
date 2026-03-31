package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.metadata.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ListNode implements ConfigNode {
  private final List<ConfigNode> elements;
  private final Metadata metadata;

  public ListNode(List<ConfigNode> elements, Metadata metadata) {
    this.elements = elements;
    this.metadata = metadata;
  }

  public ListNode(List<ConfigNode> elements) {
    this(elements, new Metadata());
  }

  public List<ConfigNode> elements() {
    return elements;
  }

  @Override
  public Metadata metadata() {
    return metadata;
  }

  @Override
  public ConfigNode deepCopy() {
    List<ConfigNode> newElements = new ArrayList<>(elements.size());
    elements.forEach(element -> newElements.add(element.deepCopy()));
    return new ListNode(newElements, metadata.copy());
  }
}
