package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.BaseHermesBuilder;
import dev.lacrid.hermes.module.BuilderModule;

public class GsonModule implements BuilderModule<BaseHermesBuilder<?>> {
  @Override
  public void configure(BaseHermesBuilder<?> builder) {
    builder.addParser(new GsonParser());
  }
}
