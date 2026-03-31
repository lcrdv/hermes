package dev.lacrid.hermes.tree;

import dev.lacrid.hermes.tree.source.Tags;

import java.util.*;

public record Scope(Set<Tags> tags) {
  public boolean includes(Tags tags) {
    return this.tags.contains(tags);
  }

  public Scope and(Tags tags) {
    HashSet<Tags> newTags = new HashSet<>(this.tags);
    newTags.add(tags);
    return new Scope(newTags);
  }

  public static Scope of(Collection<Tags> tags) {
    return new Scope(new HashSet<>(tags));
  }

  public static Scope defaultScope() {
    return new Scope(Set.of(Tags.empty()));
  }
}
