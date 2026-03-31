package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

record TreeComponents(
    List<ConfigNode> preUpdates,
    List<ConfigNode> postUpdates,
    List<LocalizedNode> overrides
) {
  static TreeComponents empty() {
    return new TreeComponents(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
  }

  static TreeComponentsBuilder builder() {
    return new TreeComponentsBuilder();
  }

  static class TreeComponentsBuilder {
    private final List<ConfigNode> preUpdates = new ArrayList<>();
    private final List<ConfigNode> postUpdates = new ArrayList<>();
    private final List<LocalizedNode> overrides = new ArrayList<>();

    public TreeComponentsBuilder preUpdate(ConfigNode preUpdate) {
      preUpdates.add(preUpdate);
      return this;
    }

    public TreeComponentsBuilder preUpdates(List<ConfigNode> preUpdates) {
      this.preUpdates.addAll(preUpdates);
      return this;
    }

    public TreeComponentsBuilder postUpdate(ConfigNode postUpdate) {
      postUpdates.add(postUpdate);
      return this;
    }

    public TreeComponentsBuilder postUpdates(List<ConfigNode> postUpdates) {
      this.postUpdates.addAll(postUpdates);
      return this;
    }

    public TreeComponentsBuilder override(LocalizedNode override) {
      overrides.add(override);
      return this;
    }

    public TreeComponentsBuilder overrides(List<LocalizedNode> overrides) {
      this.overrides.addAll(overrides);
      return this;
    }

    public TreeComponents build() {
      return new TreeComponents(preUpdates, postUpdates, overrides);
    }
  }
}
