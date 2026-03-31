package dev.lacrid.hermes.tree.source;

import java.util.Collection;
import java.util.Set;

public record Tags(Set<String> tags) {
  public static Tags of(String... tags) {
    return new Tags(Set.of(tags));
  }

  public static Tags of(Collection<String> tags) {
    return new Tags(Set.copyOf(tags));
  }

  public static Tags empty() {
    return new Tags(Set.of());
  }
}
