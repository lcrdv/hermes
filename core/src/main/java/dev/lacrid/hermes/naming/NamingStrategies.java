package dev.lacrid.hermes.naming;

import java.util.List;
import java.util.function.Function;

public enum NamingStrategies implements NamingStrategy {
  CAMEL_CASE(tokens -> {
    if (tokens.isEmpty()) {
      return "";
    }

    StringBuilder name = new StringBuilder(tokens.getFirst().toLowerCase());
    for (int i = 1; i < tokens.size(); i++) {
      String token = tokens.get(i);
      name.append(Character.toUpperCase(token.charAt(0)))
          .append(token.substring(1).toLowerCase());
    }

    return name.toString();
  }),
  SNAKE_CASE(tokens -> String.join("_", tokens).toLowerCase()),
  SCREAMING_SNAKE_CASE(tokens -> String.join("_", tokens).toUpperCase()),
  KEBAB_CASE(tokens -> String.join("-", tokens).toLowerCase()),
  SCREAMING_KEBAB_CASE(tokens -> String.join("-", tokens).toUpperCase()),
  PLAIN(tokens -> String.join("", tokens));

  private final Function<List<String>, String> strategy;

  NamingStrategies(Function<List<String>, String> strategy) {
    this.strategy = strategy;
  }

  @Override
  public String create(List<String> tokens) {
    return strategy.apply(tokens);
  }
}
