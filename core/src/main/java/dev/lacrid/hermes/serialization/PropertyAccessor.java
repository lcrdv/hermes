package dev.lacrid.hermes.serialization;

import java.lang.invoke.MethodHandle;

public interface PropertyAccessor<P, T> {
  P resolve(T parent);

  static <P, T> PropertyAccessor<P, T> fromMethodHandle(MethodHandle methodHandle) {
    return parent -> {
      if (parent == null) {
        return null;
      }

      try {
        return (P) methodHandle.invoke(parent);
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }
}