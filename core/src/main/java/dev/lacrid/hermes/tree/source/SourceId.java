package dev.lacrid.hermes.tree.source;

import java.util.UUID;

public record SourceId(UUID id) {
  public static SourceId newOne() {
    return new SourceId(UUID.randomUUID());
  }
}
