package dev.lacrid.hermes.node;

import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.metadata.Metadata;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MapNode implements ConfigNode {
  private final List<Entry> entries;
  private final Metadata metadata;
  private final Map<String, Entry> entryByKey;

  public MapNode(List<Entry> entries, Metadata metadata) {
    this.entries = new ArrayList<>(entries);
    this.metadata = metadata;
    this.entryByKey = entries.stream().collect(Collectors.toMap(
        entry -> entry.key.key().toLowerCase(), Function.identity()));
  }

  public MapNode() {
    this(Collections.emptyList());
  }

  public MapNode(List<Entry> entries) {
    this(entries, new Metadata());
  }

  public Set<NodeKey> keys() {
    return entries.stream()
        .map(Entry::key)
        .collect(Collectors.toSet());
  }

  public List<Entry> entries() {
    return Collections.unmodifiableList(entries);
  }

  public void addEntry(Entry entry) {
    entries.add(entry);
    entryByKey.put(entry.key.key().toLowerCase(), entry);
  }

  public Optional<ConfigNode> findExact(NodeKey key) {
    return findEntry(key)
        .filter(entry -> entry.key.equals(key))
        .map(Entry::node);
  }

  public Optional<ConfigNode> findByKey(NodeKey key) {
    return findEntry(key).map(Entry::node);
  }

  public Optional<Entry> findEntry(NodeKey key) {
    return Optional.ofNullable(entryByKey.get(key.key().toLowerCase()));
  }

  public Collection<ConfigNode> nodes() {
    return entries.stream()
        .map(Entry::node)
        .collect(Collectors.toList());
  }

  @Override
  public Metadata metadata() {
    return null;
  }

  @Override
  public ConfigNode deepCopy() {
    List<Entry> newEntries = new ArrayList<>(entries.size());
    for (Entry entry : entries) {
      newEntries.add(new Entry(entry.key, entry.node.deepCopy()));
    }

    return new MapNode(newEntries, metadata.copy());
  }

  public record Entry(NodeKey key, ConfigNode node) {

  }

  public static MapNode of(Map<NodeKey, ConfigNode> nodes) {
    List<Entry> entries = new ArrayList<>(nodes.size());
    nodes.forEach((key, value) -> entries.add(new Entry(key, value)));
    return new MapNode(entries);
  }
}
