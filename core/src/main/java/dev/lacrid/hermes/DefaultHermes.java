package dev.lacrid.hermes;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.ConfigException;
import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.LocalizedNode;
import dev.lacrid.hermes.node.NullNode;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.NodeWalker;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.target.ConfigWriters;
import dev.lacrid.hermes.tree.processor.ProcessorTag;
import dev.lacrid.hermes.tree.processor.TreeProcessors;
import dev.lacrid.hermes.request.ValueRequest;
import dev.lacrid.hermes.request.SaveRequest;
import dev.lacrid.hermes.tree.TreeCompiler;
import dev.lacrid.lambda.Either;

import java.util.Collections;
import java.util.List;

import static dev.lacrid.lambda.Context.either;

final class DefaultHermes implements Hermes {
  private final SourcedValueLoader sourcedValueLoader;
  private final TreeCompiler treeCompiler;
  private final NodeWalker nodeWalker;
  private final TreeProcessors treeProcessors;
  private final Serializers serializers;
  private final ConfigWriters writers;

  DefaultHermes(SourcedValueLoader sourcedValueLoader, TreeCompiler treeCompiler, NodeWalker nodeWalker, TreeProcessors treeProcessors, Serializers serializers, ConfigWriters writers) {
    this.sourcedValueLoader = sourcedValueLoader;
    this.treeCompiler = treeCompiler;
    this.nodeWalker = nodeWalker;
    this.treeProcessors = treeProcessors;
    this.serializers = serializers;
    this.writers = writers;
  }

  @Override
  public <T> T load(ValueRequest<T> request) {
    var result = sourcedValueLoader.load(request.sources(), tree(request.path()), request.type(), request.defaultValue(), request.path());
    return result.getOrElseThrow(ConfigException::new);
  }

  private SourcedValueLoader.TreeProvider tree(NodePath path) {
    return sources -> treeCompiler.build(sources, Collections.emptyList())
        .flatMap(root -> treeProcessors.byTag(ProcessorTag.READ).process(root))
        .map(root -> traverse(root, path));
  }

  @Override
  public void save(SaveRequest request) {
    Either<ConfigError, ?> result = either(ctx -> {
      List<ConfigNode> values = ctx.bind(
          Either.traverse(request.values(),
              entry -> entry.asLocalizedNode(serializers).map(LocalizedNode::node)
          ).mapLeft(ConfigError.UpdatesError::new));

      List<LocalizedNode> overrides = ctx.bind(
          Either.traverse(request.overrides(),
              entry -> entry.asLocalizedNode(serializers)
          ).mapLeft(ConfigError.OverridesError::new));

      ConfigNode root = ctx.bind(treeCompiler.build(values, overrides));
      ConfigNode processedRoot = ctx.bind(treeProcessors.byTag(ProcessorTag.WRITE).process(root));
      ConfigNode tree = traverse(processedRoot, request.path());

      return ctx.bind(Either.traverse(request.outputs(), target -> target.write(tree, writers))
          .mapLeft(ConfigError.WritingErrors::new));
    });

    result.getOrElseThrow(ConfigException::new);
  }

  private ConfigNode traverse(ConfigNode root, NodePath path) {
    return nodeWalker.resolve(root, path).orElseGet(NullNode::create);
  }
}
