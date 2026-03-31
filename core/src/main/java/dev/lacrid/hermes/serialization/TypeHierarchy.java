package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.naming.NameResolver;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.*;

public class TypeHierarchy {
  private final Map<String, Class<?>> nameToType;
  private final Map<Class<?>, String> typeToName;

  TypeHierarchy(Map<String, Class<?>> nameToType, Map<Class<?>, String> typeToName) {
    this.nameToType = nameToType;
    this.typeToName = typeToName;
  }

  public Optional<String> name(Class<?> type) {
    return Optional.ofNullable(typeToName.get(type));
  }

  public Optional<Class<?>> type(String name) {
    return Optional.ofNullable(nameToType.get(name.toLowerCase()));
  }

  public static Either<ConfigError, TypeHierarchy> from(ValueType<?> type, NameResolver resolver) {
    return new HierarchyHelper(type, resolver).build();
  }

  private static class HierarchyHelper {
    private final ValueType<?> type;
    private final NameResolver nameResolver;
    private final Map<String, Class<?>> nameToType = new HashMap<>();
    private final Map<Class<?>, String> typeToName = new HashMap<>();

    HierarchyHelper(ValueType<?> type, NameResolver nameResolver) {
      this.type = type;
      this.nameResolver = nameResolver;
    }

    Either<ConfigError, TypeHierarchy> build() {
      return collect(type).map(v -> new TypeHierarchy(nameToType, typeToName));
    }

    private Either<ConfigError, Class<?>> resolveType(TypeName annotation) {
      try {
        return Either.right(annotation.type() != Object.class ? annotation.type() : Class.forName(annotation.typeClass()));
      } catch (ClassNotFoundException e) {
        return Either.left(new ConfigError.InvalidClass(annotation.typeClass()));
      }
    }

    private Either<ConfigError, Void> collect(ValueType<?> parentType) {
      Class<?> parentClazz = parentType.clazz();
      List<TypeName> annotations = new ArrayList<>();
      parentType.annotations().find(TypeNames.class)
          .ifPresent(names -> annotations.addAll(Arrays.asList(names.value())));
      parentType.annotations().find(TypeName.class).ifPresent(annotations::add);

      Set<Class<?>> subClasses = new HashSet<>();

      for (TypeName annotation : annotations) {
        var result = resolveType(annotation)
            .filter(type -> type.isAssignableFrom(parentClazz),
                type -> new ConfigError.NotSubclass(parentClazz, type))
            .peek(subClasses::add)
            .flatMap(type -> Either.traverseRight(List.of(annotation.name()), name -> add(type, name)));

        if (result.isLeft()) {
          return Either.left(result.getLeft());
        }
      }

      if (parentClazz.isSealed()) {
        for (Class<?> type : parentClazz.getPermittedSubclasses()) {
          if (typeToName.containsKey(type)) {
            continue;
          }

          String name = nameResolver.name(type.getSimpleName());
          var result = add(type, name);
          if (result.isLeft()) {
            return result;
          }

          subClasses.add(type);
        }
      }

      for (Class<?> subClass : subClasses) {
        var result = collect(ValueType.from(subClass));
        if (result.isLeft()) {
          return result;
        }
      }

      return Either.right(null);
    }

    private Either<ConfigError, Void> add(Class<?> type, String name) {
      Class<?> previous = nameToType.put(name.toLowerCase(), type);
      if (previous != null && previous != type) {
        return Either.left(new ConfigError.DuplicateSubclassName(name, type, previous));
      }
      typeToName.putIfAbsent(type, name);
      return Either.right(null);
    }
  }
}
