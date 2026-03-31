package dev.lacrid.hermes.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

import java.util.Collections;
import java.util.List;

public final class ProcessorPipeline {
  public static ProcessorPipeline EMPTY = new ProcessorPipeline(Collections.emptyList());

  private final List<SourceProcessor> processors;

  public ProcessorPipeline(List<SourceProcessor> processors) {
    this.processors = processors;
  }

  public Either<ConfigError, ConfigNode> process(ConfigNode root) {
    ConfigNode node = root;
    for (SourceProcessor processor : processors) {
      var result = processor.process(node);
      if (result.isLeft()) {
        return result;
      }

      node = result.getRight();
    }

    return Either.right(node);
  }
}
