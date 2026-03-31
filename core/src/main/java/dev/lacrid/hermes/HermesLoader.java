package dev.lacrid.hermes;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.request.*;
import dev.lacrid.hermes.tree.LocalizedEntry;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.hermes.tree.Scope;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

public interface HermesLoader extends Hermes {
  <T> T load(ScopedValueRequest<T> request);

  <T> T bind(ValueBindRequest<T> request);

  void update(Scope scope, LocalizedEntry entry);

  void save(ScopedSaveRequest request);

  @Override
  default <T> T load(ValueRequest<T> request) {
    return load(new ScopedValueRequest<>(request.sources(), request.type(), request.defaultValue(), request.path(), Scope.defaultScope()));
  }

  @Override
  default void save(SaveRequest request) {
    save(new ScopedSaveRequest(request.outputs(), Scope.defaultScope(), request.path(), Collections.emptyList(), request.values(), request.overrides()));
  }

  default <T> T load(NodePath path, Class<T> type) {
    return load(new ScopedValueRequest<>(Collections.emptyList(), ValueType.from(type), null, path, Scope.defaultScope()));
  }

  default <T> T load(Class<T> type) {
    return load(new ScopedValueRequest<>(Collections.emptyList(), ValueType.from(type), null, NodePath.root(), Scope.defaultScope()));
  }

  static HermesLoader create() {
    return create(Function.identity());
  }

  static HermesLoader create(Function<HermesLoaderBuilder, HermesLoaderBuilder> builder) {
    return builder.apply(builder()).build();
  }

  static HermesLoaderBuilder builder() {
    return new HermesLoaderBuilder();
  }
}
