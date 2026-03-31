package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.path.NodeKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface NodeBuilder<T extends ConfigNode> {
  T build();

  static ConfigNode map(Function<MapBuilder, MapBuilder> builder) {
    return builder.apply(map()).build();
  }

  static ConfigNode list(Function<ListBuilder, ListBuilder> builder) {
    return builder.apply(list()).build();
  }

  static MapBuilder map() {
    return new MapBuilder();
  }

  static ListBuilder list() {
    return new ListBuilder();
  }

  class MapBuilder implements NodeBuilder<MapNode> {
    private final List<MapNode.Entry> entries = new ArrayList<>();

    public MapBuilder map(String key, Function<MapBuilder, MapBuilder> builder) {
      entries.add(new MapNode.Entry(NodeKey.of(key), NodeBuilder.map(builder)));
      return this;
    }

    public MapBuilder list(String key, Function<ListBuilder, ListBuilder> builder) {
      entries.add(new MapNode.Entry(NodeKey.of(key), NodeBuilder.list(builder)));
      return this;
    }

    public MapBuilder value(String key, String value) {
      entries.add(new MapNode.Entry(NodeKey.of(key), ValueNode.of(value)));
      return this;
    }

    public MapBuilder value(String key, int value) {
      entries.add(new MapNode.Entry(NodeKey.of(key), ValueNode.of(value)));
      return this;
    }

    public MapBuilder add(String key, ConfigNode node) {
      entries.add(new MapNode.Entry(NodeKey.of(key), node));
      return this;
    }

    public MapBuilder add(String key, NodeBuilder<?> builder) {
      entries.add(new MapNode.Entry(NodeKey.of(key), builder.build()));
      return this;
    }

    @Override
    public MapNode build() {
      return new MapNode(entries);
    }
  }

  class ListBuilder implements NodeBuilder<ListNode> {
    private final List<ConfigNode> nodes = new ArrayList<>();

    public ListBuilder value(String value) {
      nodes.add(ValueNode.of(value));
      return this;
    }

    public ListBuilder map(Function<MapBuilder, MapBuilder> builder) {
      nodes.add(NodeBuilder.map(builder));
      return this;
    }

    public ListBuilder list(Function<ListBuilder, ListBuilder> builder) {
      nodes.add(NodeBuilder.list(builder));
      return this;
    }

    public ListBuilder add(ConfigNode node) {
      nodes.add(node);
      return this;
    }

    public ListBuilder add(NodeBuilder<?> builder) {
      nodes.add(builder.build());
      return this;
    }

    @Override
    public ListNode build() {
      return new ListNode(nodes);
    }
  }
}
