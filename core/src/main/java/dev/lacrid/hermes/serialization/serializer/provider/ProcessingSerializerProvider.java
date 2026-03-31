package dev.lacrid.hermes.serialization.serializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.Processors;
import dev.lacrid.hermes.serialization.serializer.Serializer;
import dev.lacrid.hermes.serialization.serializer.SerializerContext;
import dev.lacrid.hermes.serialization.serializer.processor.NodeResultProcessor;
import dev.lacrid.hermes.serialization.serializer.processor.ProcessingSerializer;
import dev.lacrid.hermes.serialization.serializer.processor.ValueProcessor;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.List;

public class ProcessingSerializerProvider implements SerializerProvider {
  private final SerializerProvider delegate;
  private final List<ValueProcessor.Factory> valueProcessors;
  private final List<NodeResultProcessor.Factory> resultProcessors;

  public ProcessingSerializerProvider(SerializerProvider delegate, List<ValueProcessor.Factory> valueProcessors, List<NodeResultProcessor.Factory> resultProcessors) {
    this.delegate = delegate;
    this.valueProcessors = valueProcessors;
    this.resultProcessors = resultProcessors;
  }

  @Override
  public <T> Either<ConfigError, Serializer<T>> serializer(ValueType<T> type, SerializerContext context) {
    return delegate.serializer(type, context)
        .map(deserializer -> new ProcessingSerializer<>(deserializer,
            Processors.create(valueProcessors, ValueProcessor.Factory::create, type),
            Processors.create(resultProcessors, NodeResultProcessor.Factory::create, type)
        ));
  }
}
