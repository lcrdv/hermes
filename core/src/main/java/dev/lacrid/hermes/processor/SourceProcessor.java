package dev.lacrid.hermes.processor;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.lambda.Either;

public interface SourceProcessor {
  Either<ConfigError, ConfigNode> process(ConfigNode root);


  static SourceProcessor traversal() {
    return new SourceProcessor() {

      @Override
      public Either<ConfigError, ConfigNode> process(ConfigNode root) {
        return null; // traverse all nodes here
      }
    };
  }

  static SourceProcessor pathFlatten() {
    return new SourceProcessor() {

      @Override
      public Either<ConfigError, ConfigNode> process(ConfigNode root) {
        return null;
      }
    };
  }

  /*
  converts 0_1_2 - (needs to be started from 0 or 1 and every number available)
  meow_0_cat: "x"
  meow_1_dog: "xd"
  meow_2_wtf: "asd"
   */
  static SourceProcessor listNumberSomething() {
    return new SourceProcessor() {

      @Override
      public Either<ConfigError, ConfigNode> process(ConfigNode root) {
        return null;
      }
    };
  }


  static SourceProcessor treeReplace() {
    // ${{server.name}} or whole array/map
    return new SourceProcessor() {
      @Override
      public Either<ConfigError, ConfigNode> process(ConfigNode root) {
        return null;
      }
    };
  }
}
