package dev.lacrid.hermes;

import dev.lacrid.hermes.loader.DefaultValueLoader;
import dev.lacrid.hermes.loader.ValueLoader;
import dev.lacrid.hermes.module.BuilderModule;
import dev.lacrid.hermes.naming.NamingStrategy;
import dev.lacrid.hermes.naming.RegexLexer;
import dev.lacrid.hermes.node.path.AnnotationPathResolver;
import dev.lacrid.hermes.node.path.PathResolver;
import dev.lacrid.hermes.processor.SourceProcessor;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.serialization.deserializer.*;
import dev.lacrid.hermes.serialization.deserializer.processor.NodeProcessor;
import dev.lacrid.hermes.serialization.deserializer.processor.ValueResultProcessor;
import dev.lacrid.hermes.serialization.deserializer.provider.*;
import dev.lacrid.hermes.serialization.serializer.*;
import dev.lacrid.hermes.serialization.serializer.processor.NodeResultProcessor;
import dev.lacrid.hermes.serialization.serializer.processor.ValueProcessor;
import dev.lacrid.hermes.serialization.serializer.provider.*;
import dev.lacrid.hermes.source.loader.SequentialSourceLoader;
import dev.lacrid.hermes.source.loader.SourceLoader;
import dev.lacrid.hermes.source.parser.DefaultSourceParsers;
import dev.lacrid.hermes.source.parser.SourceParser;
import dev.lacrid.hermes.source.parser.PropertiesParser;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.hermes.target.ConfigWriter;
import dev.lacrid.hermes.tree.processor.ProcessorTag;
import dev.lacrid.hermes.tree.processor.TaggedProcessor;
import dev.lacrid.hermes.tree.processor.TreeProcessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseHermesBuilder<T extends BaseHermesBuilder<T>> {
  protected SerializationConfig serializationConfig = new SerializationConfig();

  protected List<DeserializerFactory<?>> deserializers = new ArrayList<>(DEFAULT_DESERIALIZER_FACTORIES);
  protected List<NodeProcessor.Factory> nodeProcessors = new ArrayList<>();
  protected List<ValueResultProcessor.Factory> valueResultProcessors = new ArrayList<>();
  protected boolean cacheDeserializers = true;
  protected List<Function<ValueLoader, ValueLoader>> valueLoaders = new ArrayList<>();

  protected List<SerializerFactory<?>> serializers = new ArrayList<>(DEFAULT_SERIALIZER_FACTORIES);
  protected List<ValueProcessor.Factory> valueProcessors = new ArrayList<>();
  protected List<NodeResultProcessor.Factory> nodeResultProcessors = new ArrayList<>();
  protected boolean cacheSerializers = true;

  protected List<SourceParser> parsers = new ArrayList<>(Collections.singleton(new PropertiesParser()));
  protected List<ConfigWriter> writers = new ArrayList<>();
  protected List<TaggedProcessor> sourceProcessors = new ArrayList<>();

  public T registerDeserializer(DeserializerFactory<?> factory) {
    deserializers.add(factory);
    return self();
  }

  public T registerSerializer(SerializerFactory<?> factory) {
    serializers.add(factory);
    return self();
  }

  public T nodeProcessor(NodeProcessor.Factory factory) {
    nodeProcessors.add(factory);
    return self();
  }

  public T valueLoader(Function<ValueLoader, ValueLoader> factory) {
    valueLoaders.add(factory);
    return self();
  }

  public T setNamingStrategy(NamingStrategy namingStrategy) {
    serializationConfig.setPathResolverFactory(type ->
        new AnnotationPathResolver(PathResolver.from(namingStrategy, Collections::singletonList, new RegexLexer())));
    return self();
  }

  public T serializationConfig(Consumer<SerializationConfig> consumer) {
    consumer.accept(serializationConfig);
    return self();
  }

  public T sourceLoadingProcessor(SourceProcessor processor) {
    this.sourceProcessors.add(new TaggedProcessor(processor, ProcessorTag.READ));
    return self();
  }

  public T sourceSavingProcessor(SourceProcessor processor) {
    this.sourceProcessors.add(new TaggedProcessor(processor, ProcessorTag.WRITE));
    return self();
  }

  public T addParser(SourceParser parser) {
    parsers.add(parser);
    return self();
  }

  public T addWriter(ConfigWriter writer) {
    writers.add(writer);
    return self();
  }

  public T apply(BuilderModule<? super T> module) {
    module.configure(self());
    return self();
  }

  protected TreeProcessors treeProcessors() {
    return new TreeProcessors(sourceProcessors);
  }

  protected Deserializers deserializers() {
    DeserializerProvider deserializerProvider = new ProcessingDeserializerProvider(
        new DefaultsDeserializerProvider(
            new FactoryDeserializerProvider(deserializers)), nodeProcessors, valueResultProcessors);

    if (cacheDeserializers) {
      deserializerProvider = new CachingDeserializerProvider(deserializerProvider);
    }

    return new Deserializers(deserializerProvider, serializationConfig);
  }

  protected Serializers serializers() {
    SerializerProvider serializerProvider = new ProcessingSerializerProvider(
        new NullSerializerProvider(
            new FactorySerializerProvider(serializers)), valueProcessors, nodeResultProcessors);

    if (cacheSerializers) {
      serializerProvider = new CachingSerializerProvider(serializerProvider);
    }

    return new Serializers(serializerProvider, serializationConfig);
  }

  protected SourceLoader sourceLoader(Serializers serializers) {
    SourceParsers parsers = new DefaultSourceParsers(this.parsers, serializers);
    SourceLoader sourceLoader = new SequentialSourceLoader(parsers);
    return sourceLoader;
  }

  protected ValueLoader valueLoader(Deserializers deserializers) {
    ValueLoader currentLoader = new DefaultValueLoader(deserializers);
    for (Function<ValueLoader, ValueLoader> valueLoader : valueLoaders) {
      currentLoader = valueLoader.apply(currentLoader);
    }
    return currentLoader;
  }

  protected abstract T self();

  protected static final List<DeserializerFactory<?>> DEFAULT_DESERIALIZER_FACTORIES = Arrays.asList(
      BigDecimalDeserializer.FACTORY,
      BigIntegerDeserializer.FACTORY,
      BooleanDeserializer.FACTORY,
      new ConfigDeserializerFactory(),
      DoubleDeserializer.FACTORY,
      new EnumDeserializerFactory(),
      FloatDeserializer.FACTORY,
      new HierarchyDeserializerFactory(),
      InstantDeserializer.FACTORY,
      IntegerDeserializer.FACTORY,
      new ListDeserializerFactory(),
      LongDeserializer.FACTORY,
      new MapDeserializerFactory(),
      new OptionalDeserializerFactory(),
      new ProxyDeserializerFactory(),
      new RecordDeserializerFactory(),
      new SetDeserializerFactory(),
      ShortDeserializer.FACTORY,
      StringDeserializer.FACTORY,
      UUIDDeserializer.FACTORY
  );

  protected static final List<SerializerFactory<?>> DEFAULT_SERIALIZER_FACTORIES = Arrays.asList(
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
}
