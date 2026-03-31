package dev.lacrid.hermes.module;

import dev.lacrid.hermes.BaseHermesBuilder;

public interface BuilderModule<T extends BaseHermesBuilder<?>> {
  void configure(T builder);
}
