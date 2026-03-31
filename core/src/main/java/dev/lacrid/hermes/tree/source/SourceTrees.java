package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.NodeWalker;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.tree.LocalizedEntry;
import dev.lacrid.hermes.tree.TreeCompiler;
import dev.lacrid.hermes.tree.processor.ProcessorTag;
import dev.lacrid.hermes.tree.processor.TreeProcessors;
import dev.lacrid.hermes.tree.source.reload.SourceListeners;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.tree.LocalizedValue;
import dev.lacrid.hermes.tree.Scope;
import dev.lacrid.lambda.Either;

import java.util.ArrayList;
import java.util.List;

import static dev.lacrid.lambda.Context.either;

public class SourceTrees {
  private final TreeFactoryProvider factoryProvider;
  private final SourceReadTreeProvider readTrees;
  private final TreeProcessors treeProcessors;
  private final NodeWalker nodeWalker;
  private final Deserializers deserializers;
  private final Serializers serializers;

  public SourceTrees(SourceStorage sourceStorage, TreeCompiler compiler, IdentifiableNodeProvider nodeProvider, SourceListeners listeners, TreeProcessors treeProcessors, NodeWalker nodeWalker, Deserializers deserializers, Serializers serializers) {
    this.factoryProvider = new TreeFactoryProvider(sourceStorage, nodeProvider, compiler);
    this.readTrees = new SourceReadTreeProvider(factoryProvider, treeProcessors.byTag(ProcessorTag.READ), listeners);
    this.treeProcessors = treeProcessors;
    this.nodeWalker = nodeWalker;
    this.deserializers = deserializers;
    this.serializers = serializers;
  }

  public Either<ConfigError, ConfigNode> readTree(ReadTreeLookup request) {
    List<ConfigNode> updates = request.updates();
    Scope scope = request.scope();

    if (updates.isEmpty()) {
      return readTrees.tree(scope).root();
    }

    TreeComponents components = TreeComponents.builder().postUpdates(updates).build();

    return factoryProvider.tree(scope).create(components)
        .flatMap(root -> process(root, ProcessorTag.READ))
        .map(root -> traverse(root, request.path()));
  }

  public Either<ConfigError, ConfigNode> writeTree(WriteTreeLookup request) {
    return either(ctx -> {
      List<ConfigNode> defaults = new ArrayList<>();
      TreeFactory treeFactory = factoryProvider.tree(request.scope());
      if (!request.defaults().isEmpty()) {
        ConfigNode baseRoot = ctx.bind(treeFactory.create(TreeComponents.empty())
            .flatMap(tree -> process(tree, ProcessorTag.WRITE_BASE)));
        defaults = ctx.bind(defaults(baseRoot, request.defaults()));
      }

      List<LocalizedNode> updates = ctx.bind(entries(request.updates()).mapLeft(ConfigError.UpdatesError::new));
      List<LocalizedNode> overrides = ctx.bind(entries(request.overrides()).mapLeft(ConfigError.OverridesError::new));
      TreeComponents components = TreeComponents.builder()
          .preUpdates(defaults)
          .postUpdates(updates.stream().map(LocalizedNode::asNode).toList())
          .overrides(overrides)
          .build();

      return ctx.bind(treeFactory.create(components)
          .flatMap(root -> process(root, ProcessorTag.WRITE))
          .map(root -> traverse(root, request.path())));
    });
  }

  public Either<ConfigError, Void> update(Scope scope, LocalizedEntry entry) {
    // sourceTrees.tree(scope).sourcesUpdated();
    TreeFactory tree = factoryProvider.tree(scope);
    switch (entry) {
      case LocalizedEntry.ValueEntry(LocalizedValue(NodePath path, Object value, ValueType<Object> type)) -> {
        return serializers.serialize(value, type)
            .map(node -> {
              tree.update(new LocalizedNode(path, node));
              return null;
            });
      }
      case LocalizedEntry.NodeEntry(LocalizedNode node) -> {
        tree.update(node);
        return Either.right(null);
      }
    }
  }

  private Either<ConfigError, ConfigNode> process(ConfigNode tree, ProcessorTag tag) {
    return treeProcessors.byTag(tag).process(tree);
  }

  private ConfigNode traverse(ConfigNode root, NodePath path) {
    return nodeWalker.resolve(root, path).orElseGet(NullNode::create);
  }

  private Either<ConfigError, List<ConfigNode>> defaults(ConfigNode tree, List<LocalizedValue<?>> entries) {
    return Either.traverse(entries, entry -> parseDefault(tree, (LocalizedValue<Object>) entry))
        .mapLeft(ConfigError.DefaultsError::new);
  }

  private Either<ConfigError, ConfigNode> parseDefault(ConfigNode tree, LocalizedValue<Object> entry) {
    var targetNode = traverse(tree, entry.path());
    var resultNode = deserializers.find(entry.type())
        .flatMap(deserializer -> deserializer.deserialize(targetNode, entry.value()))
        .flatMap(value -> serializers.find(entry.type())
            .flatMap(serializer -> serializer.serialize(value)));

    return resultNode.map(node -> new LocalizedNode(entry.path(), node).asNode());
  }

  private Either<List<ConfigError>, List<LocalizedNode>> entries(List<LocalizedEntry> entries) {
    return Either.traverse(entries, entry -> entry.asLocalizedNode(serializers));
  }
}
