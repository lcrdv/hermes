package dev.lacrid.hermes.serialization.deserializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.hermes.serialization.serializer.processor.ValueProcessor;
import dev.lacrid.lambda.Either;

import java.util.List;

import static dev.lacrid.hermes.serialization.Processors.process;

public class ProcessingDeserializer<T> implements Deserializer<T> {
  private final Deserializer<T> deserializer;
  private final List<NodeProcessor> nodeProcessors;
  private final List<ValueResultProcessor<T>> resultProcessors;

  public ProcessingDeserializer(
      Deserializer<T> deserializer,
      List<NodeProcessor> nodeProcessors,
      List<ValueResultProcessor<T>> resultProcessors
  ) {
    this.deserializer = deserializer;
    this.nodeProcessors = nodeProcessors;
    this.resultProcessors = resultProcessors;
  }

  @Override
  public Either<ConfigError, T> deserialize(ConfigNode receivedNode, T defaultValue) {
    return process(nodeProcessors, NodeProcessor::handle, receivedNode)
        .flatMap(processedNode -> deserializer.deserialize(processedNode, defaultValue)
            .flatMap(result -> process(resultProcessors,
                (processor, currentResult) -> processor.handle(currentResult, processedNode), result)));
  }
}
