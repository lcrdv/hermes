package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.target.ConfigWriter;
import dev.lacrid.hermes.target.WriterFormat;
import dev.lacrid.lambda.Either;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SnakeYamlWriter implements ConfigWriter {
  private final Yaml yaml;

  public SnakeYamlWriter(Yaml yaml) {
    this.yaml = yaml;
  }

  @Override
  public Either<ConfigError, Void> write(ConfigNode node, Writer writer, WriterFormat format) {
    Optional<Node> convertedNode = toYamlNode(node);
    if (convertedNode.isEmpty()) {
      return Either.left(null);
    }

    yaml.serialize(convertedNode.get(), writer);
    return Either.right(null);
  }

  @Override
  public boolean accepts(WriterFormat format) {
    return true;
  }

  private Optional<Node> toYamlNode(ConfigNode node) {
    switch (node) {
      case MapNode mapNode -> {
        List<NodeTuple> values = new ArrayList<>(mapNode.entries().size());
        for (MapNode.Entry entry : mapNode.entries()) {
          toYamlNode(entry.node()).ifPresent(entryNode -> values.add(new NodeTuple(
              new ScalarNode(Tag.STR, entry.key().key(), null, null, style()),
              entryNode
          )));
        }
        return Optional.of(new MappingNode(Tag.MAP, values, DumperOptions.FlowStyle.BLOCK));
      }
      case ListNode listNode -> {
        List<Node> values = new ArrayList<>(listNode.elements().size());
        for (ConfigNode element : listNode.elements()) {
          toYamlNode(element).ifPresent(values::add);
        }
        return Optional.of(new SequenceNode(Tag.SEQ, values, DumperOptions.FlowStyle.BLOCK));
      }
      case ValueNode valueNode -> {
        return Optional.of(toScalarNode(valueNode.holder()));
      }
      case NullNode ignored -> {
        return Optional.empty();
      }
    }
  }

  private ScalarNode toScalarNode(ValueHolder holder) {
    switch (holder) {
      case ValueHolder.StringHolder stringHolder -> {
        return new ScalarNode(Tag.STR, stringHolder.value(), null, null, style());
      }
      case ValueHolder.ShortHolder shortHolder -> {
        return new ScalarNode(Tag.INT, String.valueOf(shortHolder.value()), null, null, style());
      }
      case ValueHolder.IntegerHolder integerHolder -> {
        return new ScalarNode(Tag.INT, String.valueOf(integerHolder.value()), null, null, style());
      }
      case ValueHolder.LongHolder longHolder -> {
        return new ScalarNode(Tag.INT, String.valueOf(longHolder.value()), null, null, style());
      }
      case ValueHolder.DoubleHolder doubleHolder -> {
        return new ScalarNode(Tag.FLOAT, String.valueOf(doubleHolder.value()), null, null, style());
      }
      case ValueHolder.FloatHolder floatHolder -> {
        return new ScalarNode(Tag.FLOAT, String.valueOf(floatHolder.value()), null, null, style());
      }
      case ValueHolder.BooleanHolder booleanHolder -> {
        return new ScalarNode(Tag.BOOL, String.valueOf(booleanHolder.value()), null, null, style());
      }
      case ValueHolder.NullHolder ignored -> {
        return nullNode();
      }
    }
  }

  private ScalarNode nullNode() {
    return new ScalarNode(Tag.NULL, "null", null, null, style());
  }
  
  private DumperOptions.ScalarStyle style() {
    return DumperOptions.ScalarStyle.PLAIN;
  }
}
