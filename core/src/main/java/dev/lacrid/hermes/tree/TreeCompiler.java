package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.lambda.Either;

import java.util.List;

public interface TreeCompiler {
  Either<ConfigError, ConfigNode> build(List<ConfigNode> source, List<LocalizedNode> overrides);

  static TreeCompiler copyingCompiler() {
    return ((source, overrides) ->
        new WorkingTree(source, overrides, ConfigNode::deepCopy).compile());
  }

  static TreeCompiler inplaceCompiler() {
    return ((source, overrides) ->
        new WorkingTree(source, overrides, node -> node).compile());
  }
}
