package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.naming.RegexLexer;
import dev.lacrid.hermes.naming.NamingStrategies;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.*;
import dev.lacrid.hermes.node.NodePrinter;
import dev.lacrid.hermes.type.ValueType;

import java.util.Collections;
import java.util.function.Consumer;

public class SerializationConfig {
  private boolean ignoreEnumCase = true;
  private TypedPathResolverFactory pathResolverFactory = type -> new AnnotationPathResolver(PathResolver.from(NamingStrategies.SNAKE_CASE, Collections::singletonList, new RegexLexer()));

  public void setIgnoreEnumCase(boolean ignoreEnumCase) {
    this.ignoreEnumCase = ignoreEnumCase;
  }

  public void setPathResolverFactory(TypedPathResolverFactory pathResolverFactory) {
    this.pathResolverFactory = pathResolverFactory;
  }

  public boolean explicitOptionals() {
    return false;
  }

  public boolean treatNullAsEmpty() {
    return true;
  }

  public boolean ignoreEnumCase() {
    return ignoreEnumCase;
  }

  public boolean forceDefaultsWithEmptyNode() {
    return false;
  }

  public boolean forceDefaultsOnError() {
    return false;
  }

  public NodePrinter nodePrinter() {
    return new NodePrinter() {
      @Override
      public String node(ConfigNode node) {
        if (node instanceof ValueNode valueNode) {
          return valueNode.readString();
        }
        return "";
      }
    };
  }

  public TypedPathResolver pathResolver(ValueType<?> type) {
    return pathResolverFactory.create(type);
  }

  public static SerializationConfig configure(Consumer<SerializationConfig> configurer) {
    SerializationConfig config = new SerializationConfig();
    configurer.accept(config);
    return config;
  }
}
