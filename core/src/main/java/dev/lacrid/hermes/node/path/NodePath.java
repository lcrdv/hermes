package dev.lacrid.hermes.node.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record NodePath(List<NodeKey> keys) {
  public boolean isRoot() {
    return keys.isEmpty();
  }

  public NodePath normalize() {
    List<NodeKey> normalized = new ArrayList<>(keys.size());
    for (NodeKey key : keys) {
      normalized.add(key.normalize());
    }

    return new NodePath(normalized);
  }

  public NodePath append(NodeKey key) {
    List<NodeKey> newKeys = new ArrayList<>(keys);
    newKeys.add(key);
    return new NodePath(newKeys);
  }

  public NodeKey lastKey() {
    return keys.getLast();
  }

  public NodeKey key(int index) {
    return keys.get(index);
  }

  public NodePath subPath(int from, int to) {
    return new NodePath(keys.subList(from, to));
  }

  public NodePath parent() {
    return subPath(0, Math.max(0, keys.size() - 1));
  }

  public static NodePath root() {
    return new NodePath(Collections.emptyList());
  }

  public static NodePath of(String[] parts) {
    return new NodePath(Arrays.stream(parts).map(NodeKey::of).toList());
  }

  public static NodePath of(List<String> parts) {
    return new NodePath(parts.stream().map(NodeKey::of).toList());
  }

  public static NodePath of(String part) {
    return new NodePath(Collections.singletonList(NodeKey.of(part)));
  }
}
