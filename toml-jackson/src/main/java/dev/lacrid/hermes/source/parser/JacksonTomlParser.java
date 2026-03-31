package dev.lacrid.hermes.source.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.lambda.Either;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JacksonTomlParser extends BaseSourceParser {
  private final TomlMapper mapper;

  public JacksonTomlParser(TomlMapper mapper) {
    super(List.of("toml"));
    this.mapper = mapper;
  }

  public JacksonTomlParser() {
    this(new TomlMapper());
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format) {
    try {
      JsonNode root = mapper.readTree(reader);
      if (root == null) {
        return Either.right(NullNode.create());
      }

      return parseNode(root, format.pathResolver());
    } catch (IOException exception) {
      return Either.left(new ConfigError.ObjectInitializationError(exception.getMessage()));
    }
  }

  private Either<ConfigError, ConfigNode> parseNode(JsonNode node, PathResolver pathResolver) {
    if (node.isNull()) {
      return Either.right(ValueNode.ofNull());
    }

    if (node.isObject()) {
      return parseObject(node, pathResolver);
    }

    if (node.isArray()) {
      return parseArray(node, pathResolver);
    }

    if (node.isValueNode()) {
      return Either.right(ValueNode.of(valueHolder(node)));
    }

    return Either.left(new ConfigError.GenericNodeError("unexpected TOML node: " + node.getNodeType(), null));
  }

  private Either<ConfigError, ConfigNode> parseObject(JsonNode objectNode, PathResolver pathResolver) {
    MapNode mapNode = new MapNode();
    NodeInserter inserter = new NodeInserter(mapNode);
    Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> entry = fields.next();
      NodePath path = pathResolver.resolve(entry.getKey());
      var result = parseNode(entry.getValue(), pathResolver)
          .flatMap(parsedNode -> inserter.insert(path, parsedNode));
      if (result.isLeft()) {
        return Either.left(result.getLeft());
      }
    }

    return Either.right(mapNode);
  }

  private Either<ConfigError, ConfigNode> parseArray(JsonNode arrayNode, PathResolver pathResolver) {
    List<ConfigNode> nodes = new ArrayList<>(arrayNode.size());
    for (JsonNode child : arrayNode) {
      var result = parseNode(child, pathResolver);
      if (result.isLeft()) {
        return Either.left(result.getLeft());
      }

      nodes.add(result.getRight());
    }

    return Either.right(new ListNode(nodes));
  }

  private static ValueHolder valueHolder(JsonNode node) {
    if (node.isBoolean()) {
      return new ValueHolder.BooleanHolder(node.booleanValue());
    }

    if (node.isNumber()) {
      return switch (node.numberType()) {
        case INT -> new ValueHolder.IntegerHolder(node.intValue());
        case LONG -> new ValueHolder.LongHolder(node.longValue());
        case FLOAT -> new ValueHolder.FloatHolder(node.floatValue());
        case DOUBLE -> new ValueHolder.DoubleHolder(node.doubleValue());
        case BIG_DECIMAL, BIG_INTEGER -> new ValueHolder.StringHolder(node.asText());
      };
    }

    return new ValueHolder.StringHolder(node.asText());
  }
}
