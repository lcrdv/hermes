package dev.lacrid.hermes.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {
  private final Type type;

  protected TypeReference() {
    Type superClass = getClass().getGenericSuperclass();
    if (superClass instanceof Class<?>) {
      throw new RuntimeException("no type");
    }

    type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }

  public Type type() {
    return type;
  }
}
