package dev.lacrid.hermes.serialization.serializer.provider;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.serialization.serializer.*;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class FactorySerializerProvider implements SerializerProvider {
  private final List<SerializerFactory<?>> factories;

  public FactorySerializerProvider(List<SerializerFactory<?>> factories) {
    this.factories = new ArrayList<>(factories);
    this.factories.sort(Comparator.comparing(SerializerFactory::priority));
  }

  @Override
  public <T> Either<ConfigError, Serializer<T>> serializer(ValueType<T> type, SerializerContext context) {
    for (SerializerFactory<?> factory : factories) {
      if (!factory.supports(type)) {
        continue;
      }

      return ((SerializerFactory<T>) factory).make(type, context);
    }

    return Either.left(new ConfigError.UnknownSerializer(type));
  }

  private static final List<SerializerFactory<?>> DEFAULT_SERIALIZER_FACTORIES = Arrays.asList(
      BigDecimalSerializer.FACTORY,
      BigIntegerSerializer.FACTORY,
      BooleanSerializer.FACTORY,
      new ConfigSerializerFactory(),
      DoubleSerializer.FACTORY,
      EnumSerializer.FACTORY,
      FloatSerializer.FACTORY,
      new HierarchySerializerFactory(),
      InstantSerializer.FACTORY,
      IntegerSerializer.FACTORY,
      new ListSerializerFactory(),
      LongSerializer.FACTORY,
      new MapSerializerFactory(),
      new OptionalSerializerFactory(),
      new ProxySerializerFactory(),
      new RecordSerializerFactory(),
      StringSerializer.FACTORY,
      ShortSerializer.FACTORY,
      UUIDSerializer.FACTORY
  );

  public static FactorySerializerProvider defaults() {
    return new FactorySerializerProvider(DEFAULT_SERIALIZER_FACTORIES);
  }
}
