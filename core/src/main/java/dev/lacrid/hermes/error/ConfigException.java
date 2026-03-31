package dev.lacrid.hermes.error;

public class ConfigException extends RuntimeException {
  public ConfigException(ConfigError error) {
    super(System.lineSeparator() + "- " + error.message());
  }
}
