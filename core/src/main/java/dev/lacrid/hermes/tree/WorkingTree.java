package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.lambda.Either;

import java.util.*;
import java.util.stream.Collectors;

class WorkingTree {
  private final List<ConfigNode> source;
  private final List<LocalizedNode> overrides;
  private final Set<NodePath> overriddenPaths;
  private final CloneStrategy cloneStrategy;
  private ConfigNode root;

  WorkingTree(List<ConfigNode> source, List<LocalizedNode> overrides, CloneStrategy cloneStrategy) {
    this.overriddenPaths = overrides.stream()
        .map(override -> override.path().normalize())
        .collect(Collectors.toSet());
    this.cloneStrategy = cloneStrategy;

    ConfigNode root = null;
    if (overriddenPaths.contains(NodePath.root())) {
      List<LocalizedNode> actualOverrides = new ArrayList<>();
      for (LocalizedNode override : overrides) {
        if (!override.path().isRoot()) {
          actualOverrides.add(override);
          continue;
        }

        root = override.node();
        actualOverrides.clear();
      }

      this.source = Collections.emptyList();
      this.overrides = actualOverrides;
    } else {
      if (!source.isEmpty()) {
        root = source.getLast();
      }

      this.source = source.isEmpty() ? source : new ArrayList<>(source.subList(0, source.size() - 1));
      Collections.reverse(this.source);

      this.overrides = overrides;
    }

    this.root = root instanceof MapNode mapRoot ? cloneStrategy.clone(mapRoot) : new MapNode();
  }

  Either<ConfigError, ConfigNode> compile() {
    for (ConfigNode node : source) {
      if (!(node instanceof MapNode mapNode)) {
        continue;
      }

      root = merge(root, mapNode);
    }

    for (LocalizedNode override : overrides) {
      
    }
    
    return Either.right(root);
  }
  
  private Either<ConfigError, ConfigNode> override(ConfigNode base, LocalizedNode override) {
    ConfigNode currentNode = base;
    List<NodeKey> keys = override.path().keys();
    for (int i = 0; i < keys.size() - 1; i++) {

    }
    
    for (NodeKey key : override.path().keys()) {
      if (!(currentNode instanceof MapNode mapNode)) {
        return Either.left(null);
      }
      

    }

    return null;
  }

  private ConfigNode merge(ConfigNode base, ConfigNode mergedNode) {
    switch (base) {
      case ListNode baseList -> {
        if (!(mergedNode instanceof ListNode mergedList)) {
          return baseList;
        }
        
        return merge(baseList, mergedList);
      }
      case MapNode baseMap -> {
        if (!(mergedNode instanceof MapNode mergedMap)) {
          return baseMap;
        }
        
        return merge(baseMap, mergedMap);
      }
      case NullNode ignored -> {
        return cloneStrategy.clone(mergedNode);
      }
      case ValueNode valueNode -> {
        return valueNode;
      }
    }
  }
  
  private ListNode merge(ListNode base, ListNode mergedNode) {
    List<ConfigNode> elements = new ArrayList<>();
    List<ConfigNode> nodesToMerge = mergedNode.elements();

    for (int i = 0; i < base.elements().size(); i++) {
      ConfigNode nodeToMerge = nodesToMerge.get(i);
      ConfigNode baseNode = base.elements().get(i);
      
      if (nodeToMerge == null) {
        elements.add(baseNode);
        continue;
      }
      
      elements.add(merge(baseNode, nodeToMerge));
    }

    for (int i = base.elements().size(); i < nodesToMerge.size(); i++) {
      elements.add(cloneStrategy.clone(nodesToMerge.get(i)));
    }
    
    return new ListNode(elements, base.metadata());
  }

  private MapNode merge(MapNode base, MapNode mergedNode) {
    List<MapNode.Entry> newEntries = new ArrayList<>();
    Map<NodeKey, ConfigNode> nodesLeft = mergedNode.entries().stream()
        .collect(Collectors.toMap(entry -> entry.key().normalize(), MapNode.Entry::node));

    for (MapNode.Entry entry : base.entries()) {
      ConfigNode mergedEntryNode = nodesLeft.remove(entry.key().normalize());
      MapNode.Entry newEntry = mergedEntryNode != null
          ? new MapNode.Entry(entry.key(), merge(entry.node(), mergedEntryNode))
          : entry;

      newEntries.add(newEntry);
    }

    nodesLeft.forEach((key, node) -> newEntries.add(new MapNode.Entry(key, cloneStrategy.clone(node))));

    return new MapNode(newEntries, base.metadata());
  }

  interface CloneStrategy {
    ConfigNode clone(ConfigNode node);
  }
}
