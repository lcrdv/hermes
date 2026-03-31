package dev.lacrid.hermes.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.source.parser.SourceParsers;
import dev.lacrid.lambda.Either;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class CachedConfigSource implements ConfigSource {
  private final ConfigSource source;
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  private Either<ConfigError, ConfigNode> cachedResult;

  public CachedConfigSource(ConfigSource source) {
    this.source = source;
  }

  @Override
  public Either<ConfigError, ConfigNode> loadConfig(SourceParsers parser) {
    lock.readLock().lock();
    try {
      if (cachedResult != null) {
        return cachedResult;
      }
    } finally {
      lock.readLock().unlock();
    }

    lock.writeLock().lock();
    try {
      if (cachedResult != null) {
        return cachedResult;
      }

      var result = source.loadConfig(parser);
      if (result.isRight()) {
        cachedResult = result;
      }

      return result;
    } finally {
      lock.writeLock().unlock();
    }
  }

  public void reset() {
    lock.writeLock().lock();
    try {
      cachedResult = null;
    } finally {
      lock.writeLock().unlock();
    }
  }
}