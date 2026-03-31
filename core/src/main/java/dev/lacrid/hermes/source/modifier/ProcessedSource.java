package dev.lacrid.hermes.source.modifier;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.processor.ProcessorPipeline;
import dev.lacrid.hermes.processor.SourceProcessor;
import dev.lacrid.lambda.Either;

import java.util.List;

public final class ProcessedSource extends ConfigSourceModifier {
  private final ProcessorPipeline pipeline;

  public ProcessedSource(List<SourceProcessor> processors, ConfigSource delegate) {
    super(delegate);
    this.pipeline = new ProcessorPipeline(processors);
  }

  @Override
  protected Either<ConfigError, ConfigNode> accept(ConfigNode root) {
    return pipeline.process(root);
  }
}