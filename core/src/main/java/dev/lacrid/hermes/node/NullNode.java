package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.metadata.Metadata;

public final class NullNode implements ConfigNode {
  private final Metadata metadata;

  NullNode(Metadata metadata) {
    this.metadata = metadata;
  }

  @Override
  public Metadata metadata() {
    return metadata;
  }

  @Override
  public ConfigNode deepCopy() {
    return new NullNode(metadata);
  }

  public static NullNode create() {
    return new NullNode(new Metadata());
  }
}
