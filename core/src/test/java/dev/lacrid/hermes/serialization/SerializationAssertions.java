package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.ResultAssertions;
import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.NodeAssertions;
import dev.lacrid.hermes.node.NodeBuilder;
import dev.lacrid.lambda.Either;

public class SerializationAssertions {
  private SerializationAssertions() {
  }

  public static void assertSuccess(ConfigNode expected, Either<ConfigError, ConfigNode> result) {
    ResultAssertions.assertSuccess(result);
    NodeAssertions.assertContentEquals(expected, result.getRight());
  }

  public static void assertSuccess(NodeBuilder<?> builder, Either<ConfigError, ConfigNode> result) {
    assertSuccess(builder.build(), result);
  }
}
