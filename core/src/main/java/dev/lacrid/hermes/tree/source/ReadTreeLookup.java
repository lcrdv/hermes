package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.tree.Scope;

import java.util.List;

public record ReadTreeLookup(
    Scope scope,
    NodePath path,
    List<ConfigNode> updates
) {
}
