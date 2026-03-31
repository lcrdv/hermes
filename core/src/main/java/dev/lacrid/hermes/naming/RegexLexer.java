package dev.lacrid.hermes.naming;

import java.util.List;
import java.util.regex.Pattern;

public final class RegexLexer implements Lexer {
  private final Pattern pattern;

  public RegexLexer(Pattern pattern) {
    this.pattern = pattern;
  }

  public RegexLexer() {
    this(Pattern.compile("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])|(?<=[0-9])(?=[a-zA-Z])|[_\\-]+"));
  }

  public List<String> tokenize(String input) {
    return List.of(pattern.split(input));
  }
}
