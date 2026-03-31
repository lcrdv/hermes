package dev.lacrid.hermes.node.path;

import dev.lacrid.hermes.naming.Lexer;
import dev.lacrid.hermes.naming.NamingStrategy;

public interface PathResolver {
  NodePath resolve(String name);

  static PathResolver from(NamingStrategy namingStrategy, Lexer pathLexer, Lexer keyLexer) {
    return name -> NodePath.of(pathLexer.tokenize(name).stream()
        .map(keyLexer::tokenize)
        .map(namingStrategy::create)
        .toList());
  }

  static PathResolver separateKeys(Lexer lexer) {
    return name -> NodePath.of(lexer.tokenize(name));
  }

  static PathResolver passThrough() {
    return NodePath::of;
  }
}
