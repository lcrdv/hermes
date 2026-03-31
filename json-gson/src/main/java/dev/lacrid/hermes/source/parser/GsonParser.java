package dev.lacrid.hermes.source.parser;

import com.google.gson.*;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.lambda.Either;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonParser extends BaseSourceParser {
  private final Gson gson;

  public GsonParser(Gson gson) {
    super(List.of("json"));
    this.gson = gson;
  }

  public GsonParser() {
    this(new Gson());
  }

  @Override
  public Either<ConfigError, ConfigNode> parse(Reader reader, SourceFormat format) {
    JsonElement element = gson.fromJson(reader, JsonElement.class);
    if (element == null) {
      return Either.right(NullNode.create());
    }

    return parseNode(element, format.pathResolver());
  }

  private Either<ConfigError, ConfigNode> parseNode(JsonElement element, PathResolver pathResolver) {
    if (element.isJsonNull()) {
      return Either.right(ValueNode.ofNull());
    }

    if (element.isJsonPrimitive()) {
      return Either.right(ValueNode.of(primitiveHolder(element.getAsJsonPrimitive())));
    }

    if (element.isJsonObject()) {
      return parseObject(element.getAsJsonObject(), pathResolver);
    }

    if (element.isJsonArray()) {
      return parseArray(element.getAsJsonArray(), pathResolver);
    }

    return Either.left(null);
  }

  private Either<ConfigError, ConfigNode> parseObject(JsonObject object, PathResolver pathResolver) {
    MapNode mapNode = new MapNode();
    NodeInserter inserter = new NodeInserter(mapNode);
    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
      NodePath path = pathResolver.resolve(entry.getKey());
      var result = parseNode(entry.getValue(), pathResolver)
          .flatMap(parsedNode -> inserter.insert(path, parsedNode));
      if (result.isLeft()) {
        return Either.left(result.getLeft());
      }
    }

    return Either.right(mapNode);
  }

  private Either<ConfigError, ConfigNode> parseArray(JsonArray array, PathResolver pathResolver) {
    List<ConfigNode> nodes = new ArrayList<>(array.size());
    for (JsonElement child : array) {
      var result = parseNode(child, pathResolver);
      if (result.isLeft()) {
        return Either.left(result.getLeft());
      }

      nodes.add(result.getRight());
    }

    return Either.right(new ListNode(nodes));
  }

  private static ValueHolder primitiveHolder(JsonPrimitive primitive) {
    if (primitive.isBoolean()) {
      return new ValueHolder.BooleanHolder(primitive.getAsBoolean());
    }

    return new ValueHolder.StringHolder(primitive.getAsString());
  }
}
