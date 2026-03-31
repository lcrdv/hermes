package dev.lacrid.hermes.source.parser;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseSourceParser implements SourceParser {
  private final Set<String> acceptedFormats;

  protected BaseSourceParser(Collection<String> acceptedFormats) {
    this.acceptedFormats = acceptedFormats.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean accepts(SourceFormat format) {
    return acceptedFormats.contains(format.format().toLowerCase());
  }
}
