package dev.lacrid.hermes.naming;

import java.util.List;
import java.util.regex.Pattern;

public interface Lexer {
  List<String> tokenize(String input);

  static Lexer dotSeparated() {
    return new RegexLexer(Pattern.compile("\\."));
  }
}
