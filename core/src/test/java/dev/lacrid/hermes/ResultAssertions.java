package dev.lacrid.hermes;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.*;
import dev.lacrid.lambda.Either;
import org.junit.jupiter.api.Assertions;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ResultAssertions {
  private ResultAssertions() {
  }

  public static <T> void assertSuccess(T expected, Either<ConfigError, T> result) {
    assertTrue(result.isRight(), () -> System.lineSeparator() + result.getLeft().message());
    assertEquals(expected, result.getRight());
  }

  public static void assertSuccess(Either<ConfigError, ?> result) {
    assertTrue(result.isRight(), () -> System.lineSeparator() + result.getLeft().message());
  }

  public static void assertFailure(Class<? extends ConfigError> expected, Either<ConfigError, ?> result) {
    assertFailure(result);
    assertInstanceOf(expected, result.getLeft());
  }

  public static void assertFailure(Either<ConfigError, ?> result) {
    assertTrue(result.isLeft(), () -> "expected failure but got " + result.getRight());
  }
}
