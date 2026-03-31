package dev.lacrid.hermes.error;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodePrinter;

public final class NodeErrors {
  private final NodePrinter printer;
  private final Class<?> type;

  public NodeErrors(NodePrinter printer, Class<?> type) {
    this.printer = printer;
    this.type = type;
  }

  public ConfigError generic(ConfigNode node, String reason) {
    String nodeContent = printer.node(node);
    String message = "failed to deserialize " + type.getSimpleName() + " from '" + nodeContent + "'"
        + (reason != null && !reason.isEmpty() ? ": " + reason : "");
    return new ConfigError.GenericNodeError(message, null);
  }

  public ConfigError generic(ConfigNode node) {
    return generic(node, null);
  }

  public ConfigError replace(ConfigNode node, String message) {
    String nodeContent = printer.node(node);
    String typeName = type.getSimpleName();

    return new ConfigError.GenericNodeError(message
        .replace("{node}", nodeContent)
        .replace("{type}", typeName), null);
  }
}