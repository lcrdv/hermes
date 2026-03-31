package dev.lacrid.hermes.request;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.target.ConfigOutput;
import dev.lacrid.hermes.tree.LocalizedEntry;

import java.util.List;

public record SaveRequest(
    List<ConfigOutput> outputs,
    NodePath path,
    List<LocalizedEntry> values,
    List<LocalizedEntry> overrides
) {
}
