package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.MapNode;
import dev.lacrid.hermes.node.NodeAssertions;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.node.path.NodeKey;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.lacrid.hermes.node.NodeBuilder.list;
import static dev.lacrid.hermes.node.NodeBuilder.map;
import static dev.lacrid.hermes.serialization.SerializationAssertions.assertSuccess;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class TreeCompilerTest {
  private final TreeCompiler compiler = TreeCompiler.copyingCompiler();

  @Test
  void buildsEmptyMapWhenThereAreNoSourceNodes() {
    assertSuccess(map(), compiler.build(List.of(), List.of()));
  }

  @Test
  void clonesSingleSource() {
    ConfigNode source = map(builder -> builder
        .value("name", "hermes")
        .map("source", nested -> nested.value("kind", "file")));

    var result = compiler.build(List.of(source), List.of());

    assertSuccess(source, result);
    assertNotSame(source, result.getRight());
  }

  @Test
  void lastSourceWinsWhenValuesConflict() {
    ConfigNode earlier = map(builder -> builder
        .value("name", "default")
        .value("host", "localhost"));
    ConfigNode later = map(builder -> builder
        .value("name", "local"));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .value("name", "local")
            .value("host", "localhost")),
        result
    );
  }

  @Test
  void multipleSourcesMergePriority() {
    ConfigNode base = map(builder -> builder
        .value("name", "base")
        .map("service", service -> service
            .value("host", "base.internal")
            .value("port", 8080)
            .value("protocol", "http")));
    ConfigNode env = map(builder -> builder
        .value("name", "env")
        .map("service", service -> service
            .value("host", "env.internal")
            .value("port", 8081)));
    ConfigNode local = map(builder -> builder
        .value("name", "local")
        .map("service", service -> service
            .value("host", "127.0.0.1")));

    var result = compiler.build(List.of(base, env, local), List.of());

    assertSuccess(map(builder -> builder
            .value("name", "local")
            .map("service", service -> service
                .value("host", "127.0.0.1")
                .value("port", 8081)
                .value("protocol", "http"))),
        result);
  }

  @Test
  void mergesNestedMapsRecursively() {
    ConfigNode earlier = map(builder -> builder
        .map("service", service -> service
            .value("host", "localhost")
            .value("port", 8080))
        .value("environment", "dev"));
    ConfigNode later = map(builder -> builder
        .map("service", service -> service
            .value("host", "prod.internal")));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .map("service", service -> service
                .value("host", "prod.internal")
                .value("port", 8080))
            .value("environment", "dev")),
        result);
  }

  @Test
  void usesLaterCasingForDuplicateKeys() {
    ConfigNode earlier = map(builder -> builder
        .map("Sources", sources -> sources
            .value("base", "defaults.yml")));
    ConfigNode later = map(builder -> builder
        .map("sourceS", sources -> sources
            .value("local", "local.yml")));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .map("sourceS", sources -> sources
                .value("local", "local.yml")
                .value("base", "defaults.yml"))),
        result);
  }

  @Test
  void mergesListsByIndex() {
    ConfigNode earlier = map(builder -> builder
        .add("sources", list(elements -> elements
            .value("base")
            .value("shared")
            .value("fallback"))));
    ConfigNode later = map(builder -> builder
        .add("sources", list(elements -> elements
            .value("local")
            .value("override"))));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .add("sources", list(elements -> elements
                .value("local")
                .value("override")
                .value("fallback")))),
        result);
  }

  @Test
  void mergesListsOfMapsByIndex() {
    ConfigNode earlier = map(builder -> builder
        .add("sources", list(elements -> elements
            .map(source -> source
                .value("name", "default")
                .value("path", "/etc/default"))
            .map(source -> source
                .value("name", "shared")
                .value("enabled", "true"))
            .map(source -> source
                .value("name", "fallback")
                .value("path", "/etc/fallback")))));
    ConfigNode later = map(builder -> builder
        .add("sources", list(elements -> elements
            .map(source -> source
                .value("name", "local"))
            .map(source -> source
                .value("enabled", "false")))));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .add("sources", list(elements -> elements
                .map(source -> source
                    .value("name", "local")
                    .value("path", "/etc/default"))
                .map(source -> source
                    .value("name", "shared")
                    .value("enabled", "false"))
                .map(source -> source
                    .value("name", "fallback")
                    .value("path", "/etc/fallback"))))),
        result);
  }

  @Test
  void treeDoesNotChangeWhenSourceIsMutated() {
    MapNode source = (MapNode) map(builder -> builder
        .map("service", service -> service
            .value("host", "localhost"))
        .add("sources", list(elements -> elements
            .map(entry -> entry.value("name", "base")))));

    var result = compiler.build(List.of(source), List.of());

    source.addEntry(new MapNode.Entry(NodeKey.of("newKey"), ValueNode.of("mutated")));

    NodeAssertions.assertContentEquals(map(builder -> builder
            .map("service", service -> service
                .value("host", "localhost"))
            .add("sources", list(elements -> elements
                .map(entry -> entry.value("name", "base"))))),
        result.getRight());
  }

  @Test
  void mergesListsOfMaps() {
    ConfigNode earlier = map(builder -> builder
        .add("sources", list(elements -> elements
            .map(source -> source
                .value("name", "base")
                .value("path", "/etc/base")
                .value("priority", "1"))
            .map(source -> source
                .value("name", "shared")
                .value("path", "/etc/shared")
                .value("priority", "2")))));
    ConfigNode later = map(builder -> builder
        .add("sources", list(elements -> elements
            .map(source -> source
                .value("path", "/workspace/base"))
            .map(source -> source
                .value("priority", "20")))));

    var result = compiler.build(List.of(earlier, later), List.of());

    assertSuccess(map(builder -> builder
            .add("sources", list(elements -> elements
                .map(source -> source
                    .value("name", "base")
                    .value("path", "/workspace/base")
                    .value("priority", "1"))
                .map(source -> source
                    .value("name", "shared")
                    .value("path", "/etc/shared")
                    .value("priority", "20"))))),
        result);
  }
}
