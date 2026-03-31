package dev.lacrid.hermes.request;

import dev.lacrid.hermes.node.path.NodePath;
import dev.lacrid.hermes.source.ConfigSource;
import dev.lacrid.hermes.type.TypeReference;
import dev.lacrid.hermes.type.ValueType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ValueRequest<T>(
    List<ConfigSource> sources,
    ValueType<T> type,
    T defaultValue,
    NodePath path
) {
  public ValueRequest {
    Objects.requireNonNull(sources);
    Objects.requireNonNull(type);
    Objects.requireNonNull(path);
  }

  public static class Builder<T> {
    private final List<ConfigSource> sources = new ArrayList<>();
    private final ValueType<T> type;
    private T defaultValue = null;
    private NodePath path = NodePath.root();

    public Builder(ValueType<T> type) {
      this.type = type;
    }

    public Builder<T> source(ConfigSource source) {
      sources.add(source);
      return this;
    }

    public Builder<T> defaultValue(T defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder<T> path(NodePath path) {
      this.path = path;
      return this;
    }

    public ValueRequest<T> build() {
      return new ValueRequest<>(sources, type, defaultValue, path);
    }
  }

  public static class TypedBuilder {
    public <T> ValueRequest.Builder<T> type(ValueType<T> type) {
      return new Builder<>(type);
    }

    public <T> ValueRequest.Builder<T> type(Class<T> type) {
      return new Builder<>(ValueType.from(type));
    }

    public <T> ValueRequest.Builder<T> type(TypeReference<T> typeReference) {
      return new Builder<>(ValueType.from(typeReference));
    }
  }

  public static <T> Builder<T> type(ValueType<T> type) {
    return new Builder<>(type);
  }

  public static <T> Builder<T> type(Class<T> type) {
    return new Builder<>(ValueType.from(type));
  }

  public static <T> Builder<T> type(TypeReference<T> typeReference) {
    return new Builder<>(ValueType.from(typeReference));
  }

  public static <T> Builder<T> builder(Class<T> type) {
    return new Builder<>(ValueType.from(type));
  }
}
