package dev.lacrid.hermes.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class FieldScanner {
  public static Predicate<Field> INSTANCE = field -> {
    int modifiers = field.getModifiers();
    return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
  };
  public static Predicate<Field> STATIC = field -> Modifier.isStatic(field.getModifiers());

  private final List<Field> fields;

  public FieldScanner(Class<?> type) {
    this.fields = collectAllFields(type);
  }

  public List<Field> findFields(Predicate<Field> predicate) {
    return fields.stream().filter(predicate).toList();
  }

  private static List<Field> collectAllFields(Class<?> type) {
    List<Field> fields = new ArrayList<>(collectFields(type));

    Class<?> superclass = type;
    while (superclass.getSuperclass() != null) {
      superclass = superclass.getSuperclass();
      fields.addAll(collectFields(superclass));
    }

    return fields;
  }

  private static List<Field> collectFields(Class<?> clazz) {
    return List.of(clazz.getDeclaredFields());
  }
}
