package dev.lacrid.hermes.serialization.deserializer.object;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.node.path.TypedPathResolver;
import dev.lacrid.hermes.node.path.fetcher.NodeFetcher;
import dev.lacrid.hermes.serialization.ObjectProperty;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.hermes.serialization.deserializer.DeserializerContext;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InitializerArgument;
import dev.lacrid.hermes.serialization.deserializer.object.initializer.InitializerSpec;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.*;

import static dev.lacrid.lambda.Context.either;

public class ObjectDeserializerFactory<T> {
  private final DeserializerContext context;

  public ObjectDeserializerFactory(DeserializerContext context) {
    this.context = context;
  }

  public Either<List<ConfigError>, Deserializer<T>> create(
      List<ObjectProperty<?, T>> objectProperties,
      List<InitializerSpec<T>> initializers,
      DefaultValue<T> defaultValue,
      ValueType<T> type
  ) {
    return either(ctx -> {
      SerializationConfig config = context.config();
      TypedPathResolver typedPathResolver = config.pathResolver(type);

      List<DeserializableProperty<Object, T>> properties = ctx.bind(objectProperties.stream().map(property -> {
        NodePath path = typedPathResolver.resolve(property.name(), property.type());
        return context.deserializers().find(property.type()).biMap(
            error -> new ConfigError.KeyedError(property.name(), error), // error with just string
            deserializer -> new DeserializableProperty<>((ObjectProperty<Object, T>) property, (Deserializer<Object>) deserializer, path)
        );
      }).collect(Either.collector()));

      Properties<T> propertiesMap = Properties.from(properties);

      Map<BitSet, ResolvedInitializer<T>> initializersMap = new HashMap<>();
      ResolvedInitializer<T> fallbackInitializer = null;
      int bestPrimaryResolvers = -1;

      for (InitializerSpec<T> initializer : initializers) {
        List<ObjectPropertyDeserializer<?, T>> initializerResolvers = new ArrayList<>(initializer.arguments().size());
        BitSet usedResolvers = new BitSet(propertiesMap.fetchers.size());
        int primaryResolvers = 0;
        for (InitializerArgument argument : initializer.arguments()) {
          Optional<IndexedResolver<T>> resolver = propertiesMap.query(argument);
          if (resolver.isEmpty()) {
            // TODO: allow for custom arguments (not limited by properties)
            ctx.raise(Collections.singletonList(new ConfigError.UnknownInitializerProperty(argument.name(), argument.type())));
          }
          IndexedResolver<T> indexedResolver = resolver.get();
          initializerResolvers.add(indexedResolver.resolver);
          usedResolvers.set(indexedResolver.index);
          if (indexedResolver.index < properties.size()) {
            primaryResolvers++;
          }
        }

        ResolvedInitializer<T> container = new ResolvedInitializer<>(initializer.initializer(), initializerResolvers);
        initializersMap.putIfAbsent(usedResolvers, container);
        if (primaryResolvers > bestPrimaryResolvers) {
          fallbackInitializer = container;
          bestPrimaryResolvers = primaryResolvers;
        }
      }

      if (fallbackInitializer == null) {
        ctx.raise(Collections.singletonList(new ConfigError.MissingInitializer()));
      }

      return new ObjectDeserializer<>(propertiesMap.fetchers, initializersMap, fallbackInitializer, defaultValue, type.clazz());
    });
  }

  private record Properties<T>(
      List<NodeFetcher> fetchers,
      Map<Class<?>, Map<String, IndexedResolver<T>>> propertyQuery
  ) {
    public Optional<IndexedResolver<T>> query(InitializerArgument argument) {
      Map<String, IndexedResolver<T>> properties = propertyQuery.get(argument.type().clazz());
      if (properties == null) {
        return Optional.empty();
      }
      return Optional.ofNullable(properties.get(argument.name().toLowerCase()));
    }

    static <T> Properties<T> from(List<DeserializableProperty<Object, T>> properties) {
      List<NodeFetcher> fetchers = new ArrayList<>(properties.size());
      Map<Class<?>, Map<String, IndexedResolver<T>>> propertyQuery = new HashMap<>();

      for (int index = 0; index < properties.size(); index++) {
        DeserializableProperty<Object, T> deserializableProperty = properties.get(index);
        ObjectProperty<Object, T> property = deserializableProperty.property;
        ObjectPropertyDeserializer<Object, T> resolver = new ObjectPropertyDeserializer<>(deserializableProperty.deserializer, property.accessor(), index, deserializableProperty.path);
        IndexedResolver<T> indexedResolver = new IndexedResolver<>(index, resolver);

        propertyQuery.computeIfAbsent(property.type().clazz(), key -> new HashMap<>()).put(property.name().toLowerCase(), indexedResolver);
        fetchers.add(NodeFetcher.exactMatch(deserializableProperty.path));
      }

      return new Properties<>(fetchers, propertyQuery);
    }
  }

  private record IndexedResolver<T>(int index, ObjectPropertyDeserializer<?, T> resolver) {
  }

  private record DeserializableProperty<V, T>(ObjectProperty<V, T> property, Deserializer<V> deserializer, NodePath path) {
  }
}
