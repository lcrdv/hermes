package dev.lacrid.hermes.naming;

import java.util.List;

public interface NamingStrategy {
  String create(List<String> tokens);
}
