package dev.lacrid.hermes;

import dev.lacrid.hermes.loader.SourcedValueLoader;
import dev.lacrid.hermes.loader.ValueLoader;
import dev.lacrid.hermes.node.NodeWalker;
import dev.lacrid.hermes.serialization.deserializer.Deserializers;
import dev.lacrid.hermes.serialization.serializer.Serializers;
import dev.lacrid.hermes.source.loader.SourceLoader;
import dev.lacrid.hermes.target.ConfigWriters;
import dev.lacrid.hermes.target.DefaultConfigWriters;
import dev.lacrid.hermes.tree.processor.TreeProcessors;
import dev.lacrid.hermes.tree.TreeCompiler;

public class HermesBuilder extends BaseHermesBuilder<HermesBuilder> {

  @Override
  protected HermesBuilder self() {
    return this;
  }

  public Hermes build() {
    Deserializers deserializers = deserializers();
    Serializers serializers = serializers();

    ValueLoader valueLoader = valueLoader(deserializers);
    SourceLoader sourceLoader = sourceLoader(serializers);
    SourcedValueLoader sourcedValueLoader = new SourcedValueLoader(sourceLoader, valueLoader);

    TreeProcessors treeProcessors = treeProcessors();
    TreeCompiler treeCompiler = TreeCompiler.copyingCompiler();
    NodeWalker nodeWalker = NodeWalker.visitingWalker();

    ConfigWriters writers = new DefaultConfigWriters(this.writers);

    return new DefaultHermes(
        sourcedValueLoader,
        treeCompiler,
        nodeWalker,
        treeProcessors,
        serializers,
        writers
    );
  }
}
