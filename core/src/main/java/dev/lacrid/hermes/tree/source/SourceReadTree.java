package dev.lacrid.hermes.tree.source;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.processor.ProcessorPipeline;
import dev.lacrid.hermes.tree.source.reload.SourceReloadListener;
import dev.lacrid.lambda.Either;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class SourceReadTree implements SourceReloadListener {
  private final TreeFactory treeFactory;
  private final ProcessorPipeline processors;
  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private ConfigNode root = null;

  SourceReadTree(TreeFactory treeFactory, ProcessorPipeline processors) {
    this.treeFactory = treeFactory;
    this.processors = processors;
  }

  Either<ConfigError, ConfigNode> root() {
    lock.readLock().lock();

    if (root == null) {
      lock.readLock().unlock();
      lock.writeLock().lock();

      try {
        if (root == null) {

          Either<ConfigError, ConfigNode> result = treeFactory
              .create(TreeComponents.empty())
              .flatMap(processors::process);

          if (result.isRight()) {
            root = result.getRight();
          }

          return result;
        }

        lock.readLock().lock();
      } finally {
        lock.writeLock().unlock();
      }
    }

    try {
      return Either.right(root);
    } finally {
      lock.readLock().unlock();
    }
  }

  @Override
  public void sourceReloaded() {
    lock.writeLock().lock();
    try {
      root = null;
    } finally {
      lock.writeLock().unlock();
    }
  }
}
