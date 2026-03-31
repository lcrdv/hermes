package dev.lacrid.hermes.serialization.deserializer.object;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.LocalizedError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.fetcher.NodeFetcher;
import dev.lacrid.hermes.serialization.deserializer.Deserializer;
import dev.lacrid.lambda.Either;

import java.util.*;

class ObjectDeserializer<T> implements Deserializer<T> {
  private final List<NodeFetcher> fetchers;
  private final Map<BitSet, ResolvedInitializer<T>> initializers;
  private final ResolvedInitializer<T> fallbackInitializer;
  private final DefaultValue<T> defaultValue;
  private final Class<T> type;

  ObjectDeserializer(List<NodeFetcher> fetchers, Map<BitSet, ResolvedInitializer<T>> initializers, ResolvedInitializer<T> fallbackInitializer, DefaultValue<T> defaultValue, Class<T> type) {
    this.fetchers = fetchers;
    this.initializers = initializers;
    this.fallbackInitializer = fallbackInitializer;
    this.defaultValue = defaultValue;
    this.type = type;
  }

  @Override
  public Either<ConfigError, T> deserialize(ConfigNode node, T receivedDefault) {
    if (!(node instanceof MapNode mapNode)) {
      return Either.left(ConfigError.unexpectedNode(MapNode.class, node));
    }

    T defaultInstance = receivedDefault != null ? receivedDefault : defaultValue.instance();

    List<ConfigNode> nodes = new ArrayList<>(fetchers.size());
    BitSet availableNodes = new BitSet(fetchers.size());

    for (int i = 0; i < fetchers.size(); i++) {
      NodeFetcher fetcher = fetchers.get(i);
      Optional<ConfigNode> fetchedNode = fetcher.fetch(mapNode);

      nodes.add(fetchedNode.orElse(NullNode.create()));
      if (fetchedNode.isPresent()) {
        availableNodes.set(i);
      }
    }

    ResolvedInitializer<T> initializer = initializers.getOrDefault(availableNodes, fallbackInitializer);

    return initializer.properties().stream()
        .map(property -> property.deserialize(nodes, defaultInstance))
        .collect(Either.collector())
        .mapLeft(LocalizedError::flatten)
        .flatMap(arguments -> initializer.initializer().initialize((List<Object>) arguments).mapLeft(Collections::singletonList))
        .mapLeft(errors -> new ConfigError.TypedDeserializeErrors(type, errors));
  }
}
