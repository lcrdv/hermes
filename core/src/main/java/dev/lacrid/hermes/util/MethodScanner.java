package dev.lacrid.hermes.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class MethodScanner {
  public static Predicate<Method> SETTERS = method -> method.getReturnType().equals(Void.TYPE)
      && method.getParameterCount() == 1;

  public static Predicate<Method> GETTERS = method -> !method.getReturnType().equals(Void.TYPE)
      && method.getParameterCount() == 0;

  private final List<Method> methods;

  public MethodScanner(Class<?> type) {
    Map<String, Method> methods = new HashMap<>();
    collectMethods(type, methods);
    this.methods = new ArrayList<>(methods.values());
  }

  public List<Method> findMethods(Predicate<Method> predicate) {
    return methods.stream().filter(predicate).toList();
  }

  private static void collectMethods(Class<?> type, Map<String, Method> methods) {
    for (Method declaredMethod : type.getDeclaredMethods()) {
      methods.putIfAbsent(declaredMethod.getName(), declaredMethod);
    }

    if (type.isInterface()) {
      for (Class<?> anInterface : type.getInterfaces()) {
        collectMethods(anInterface, methods);
      }
    }

    if (type.getSuperclass() != null) {
      collectMethods(type.getSuperclass(), methods);
    }
  }
}
