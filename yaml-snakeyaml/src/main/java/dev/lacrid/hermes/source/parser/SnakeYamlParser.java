package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.lambda.Either;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.*;

import java.io.Reader;
import java.util.*;
import java.util.function.Function;

public class SnakeYamlParser extends BaseSourceParser {
  private final Yaml yaml;

  public SnakeYamlParser(Yaml yaml) {
    super(List.of("yaml", "yml"));
    this.yaml = yaml;
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format) {
    MappingNode rootNode = (MappingNode) yaml.compose(reader);
    return parseNode(rootNode, format.pathResolver());
  }

  private Either<ConfigError, ConfigNode> parseNode(Node node, PathResolver pathResolver) {
    switch (node) {
      case ScalarNode scalar -> {
        return Either.right(ValueNode.of(scalarHolder(scalar)));
      }
      case MappingNode mapping -> {
        MapNode mapNode = new MapNode();
        NodeInserter inserter = new NodeInserter(mapNode);
        for (NodeTuple child : mapping.getValue()) {
          if (!(child.getKeyNode() instanceof ScalarNode keyNode)) {
            throw new IllegalStateException("expected ScalarNode but got " + child.getKeyNode());
          }

          NodePath path = pathResolver.resolve(keyNode.getValue());
          var result = parseNode(child.getValueNode(), pathResolver)
              .flatMap(parsedNode -> inserter.insert(path, parsedNode));
          if (result.isLeft()) {
            return Either.left(result.getLeft());
          }
        }
        return Either.right(mapNode);
      }
      case SequenceNode collection -> {
        List<ConfigNode> nodes = new ArrayList<>();
        for (Node child : collection.getValue()) {
          var result = parseNode(child, pathResolver);
          if (result.isLeft()) {
            return Either.left(result.getLeft());
          }
          nodes.add(result.getRight());
        }
        return Either.right(new ListNode(nodes));
      }
      default -> throw new IllegalStateException("unexpected value: " + node);
    }
  }

  private static final Function<String, ValueHolder> DEFAULT_MAPPER = ValueHolder.StringHolder::new;
  private static final Map<String, Function<String, ValueHolder>> TAG_MAPPERS = Map.of(
      "tag:yaml.org,2002:int", text -> new ValueHolder.IntegerHolder(Integer.parseInt(text)),
      "tag:yaml.org,2002:float", text -> new ValueHolder.FloatHolder(Float.parseFloat(text)),
      "tag:yaml.org,2002:bool", text -> new ValueHolder.BooleanHolder(Boolean.parseBoolean(text)),
      "tag:yaml.org,2002:null", text -> new ValueHolder.NullHolder()
  );

  private static ValueHolder scalarHolder(ScalarNode node) {
    String tag = node.getTag().getValue();
    return TAG_MAPPERS.getOrDefault(tag, DEFAULT_MAPPER).apply(node.getValue());
  }
}
