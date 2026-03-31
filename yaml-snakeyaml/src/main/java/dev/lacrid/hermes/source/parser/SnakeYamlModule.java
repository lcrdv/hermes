package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.BaseHermesBuilder;
import dev.lacrid.hermes.module.BuilderModule;
import org.yaml.snakeyaml.Yaml;

public class SnakeYamlModule implements BuilderModule<BaseHermesBuilder<?>> {
  private final Yaml yaml;

  public SnakeYamlModule(Yaml yaml) {
    this.yaml = yaml;
  }

  public SnakeYamlModule() {
    this(new Yaml());
  }

  @Override
  public void configure(BaseHermesBuilder<?> builder) {
    builder.addParser(new SnakeYamlParser(yaml));
    builder.addWriter(new SnakeYamlWriter(yaml));
  }
}
