package dev.lacrid.hermes.serialization.deserializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.Processors;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.hermes.serialization.deserializer.DeserializerContext;
import dev.lacrid.hermes.serialization.deserializer.processor.NodeProcessor;
import dev.lacrid.hermes.serialization.deserializer.processor.ProcessingDeserializer;
import dev.lacrid.hermes.serialization.deserializer.processor.ValueResultProcessor;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;

public class ProcessingDeserializerProvider implements DeserializerProvider {
  private final DeserializerProvider delegate;
  private final List<NodeProcessor.Factory> nodeProcessors;
  private final List<ValueResultProcessor.Factory> resultProcessors;

  public ProcessingDeserializerProvider(DeserializerProvider delegate, List<NodeProcessor.Factory> nodeProcessors, List<ValueResultProcessor.Factory> resultProcessors) {
    this.delegate = delegate;
    this.nodeProcessors = nodeProcessors;
    this.resultProcessors = resultProcessors;
  }

  @Override
  public <T> Either<ConfigError, Deserializer<T>> deserializer(ValueType<T> type, DeserializerContext context) {
    return delegate.deserializer(type, context)
        .map(deserializer -> new ProcessingDeserializer<>(deserializer,
            Processors.create(nodeProcessors, NodeProcessor.Factory::create, type),
            Processors.create(resultProcessors, ValueResultProcessor.Factory::create, type))
        );
  }
}
