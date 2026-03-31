package dev.lacrid.hermes.request;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.target.ConfigOutput;
import dev.lacrid.hermes.tree.LocalizedValue;
import dev.lacrid.hermes.tree.Scope;
import dev.lacrid.hermes.tree.LocalizedEntry;

import java.util.List;

public record ScopedSaveRequest(
    List<ConfigOutput> outputs,
    Scope scope,
    NodePath path,
    List<LocalizedValue<?>> defaults,
    List<LocalizedEntry> updates,
    List<LocalizedEntry> overrides
) {
}
