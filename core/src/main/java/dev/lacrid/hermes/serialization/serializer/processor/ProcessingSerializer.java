package dev.lacrid.hermes.serialization.serializer.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.lambda.Either;

import java.util.List;
import java.util.function.BiFunction;

import static dev.lacrid.hermes.serialization.Processors.process;

public class ProcessingSerializer<T> implements Serializer<T> {
  private final Serializer<T> serializer;
  private final List<ValueProcessor<T>> valueProcessors;
  private final List<NodeResultProcessor<T>> resultProcessors;

  public ProcessingSerializer(Serializer<T> serializer, List<ValueProcessor<T>> valueProcessors, List<NodeResultProcessor<T>> resultProcessors) {
    this.serializer = serializer;
    this.valueProcessors = valueProcessors;
    this.resultProcessors = resultProcessors;
  }

  @Override
  public Either<ConfigError, ConfigNode> serialize(T value) {
    return process(valueProcessors, ValueProcessor::handle, value)
        .flatMap(processedValue -> serializer.serialize(processedValue)
            .flatMap(result -> process(resultProcessors,
                (processor, currentResult) -> processor.handle(currentResult, processedValue), result)));
  }
}
