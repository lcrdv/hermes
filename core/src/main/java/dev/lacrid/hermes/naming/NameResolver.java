package dev.lacrid.hermes.naming;

public final class NameResolver {
  private final Lexer lexer;
  private final NamingStrategy namingStrategy;

  public NameResolver(Lexer lexer, NamingStrategy namingStrategy) {
    this.lexer = lexer;
    this.namingStrategy = namingStrategy;
  }

  public String name(String input) {
    return namingStrategy.create(lexer.tokenize(input));
  }
}
