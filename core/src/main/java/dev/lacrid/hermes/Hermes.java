package dev.lacrid.hermes;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.request.ValueRequest;
import dev.lacrid.hermes.request.SaveRequest;

import java.util.List;
import java.util.function.Function;

public interface Hermes {
  <T> T load(ValueRequest<T> request);

  void save(SaveRequest request);

  default <T> T load(Function<ValueRequest.TypedBuilder, ValueRequest.Builder<T>> builder) {
    return load(builder.apply(new ValueRequest.TypedBuilder()).build());
  }

  default <T> T load(List<ConfigSource> sources, NodePath path, Class<T> type, T defaultValue) {
    return load(new ValueRequest<>(sources, ValueType.from(type), defaultValue, path));
  }

  default <T> T load(ConfigSource source, NodePath path, Class<T> type, T defaultValue) {
    return load(new ValueRequest<>(List.of(source), ValueType.from(type), defaultValue, path));
  }

  default <T> T load(ConfigSource source, NodePath path, Class<T> type) {
    return load(r -> r.type(type).source(source).path(path));
  }

  default <T> T load(ConfigSource source, Class<T> type, T defaultValue) {
    return load(r -> r.type(type).source(source).defaultValue(defaultValue));
  }

  default <T> T load(ConfigSource source, Class<T> type) {
    return load(r -> r.type(type).source(source));
  }

  default <T> T load(List<ConfigSource> sources, NodePath path, TypeReference<T> type, T defaultValue) {
    return load(new ValueRequest<>(sources, ValueType.from(type), defaultValue, path));
  }

  default <T> T load(ConfigSource source, NodePath path, TypeReference<T> type, T defaultValue) {
    return load(new ValueRequest<>(List.of(source), ValueType.from(type), defaultValue, path));
  }

  default <T> T load(ConfigSource source, NodePath path, TypeReference<T> type) {
    return load(r -> r.type(type).source(source).path(path));
  }

  default <T> T load(ConfigSource source, TypeReference<T> type, T defaultValue) {
    return load(r -> r.type(type).source(source).defaultValue(defaultValue));
  }

  default <T> T load(ConfigSource source, TypeReference<T> type) {
    return load(r -> r.type(type).source(source));
  }

  static Hermes create() {
    return create(Function.identity());
  }

  static Hermes create(Function<HermesBuilder, HermesBuilder> builder) {
    return builder.apply(builder()).build();
  }

  static HermesBuilder builder() {
    return new HermesBuilder();
  }
}
