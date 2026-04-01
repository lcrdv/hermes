package dev.lacrid.hermes.error;

import dev.lacrid.hermes.node.*;
import dev.lacrid.hermes.node.path.NodeKey;
import dev.lacrid.hermes.type.ValueType;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ConfigError {
  String message();

  class GenericNodeError implements ConfigError {
    private final String message;

    public GenericNodeError(String message, ConfigNode node) {
      String location = null;
      this.message = message + (location != null ? " (" + location + ")" : "");
    }

    @Override
    public String message() {
      return message;
    }
  }

  static ConfigError unexpectedNode(Set<Class<? extends ConfigNode>> expected, ConfigNode received) {
    if (received instanceof NullNode) {
      return new MissingNode();
    }

    String expectedNodes = expected.stream()
        .map(ConfigError::nodeName)
        .collect(Collectors.joining(", "));
    String receivedNode = nodeName(received.getClass());
    return new GenericNodeError("expected %s but received %s".formatted(expectedNodes, receivedNode), received);
  }

  static ConfigError unexpectedNode(Class<? extends ConfigNode> expected, ConfigNode received) {
    return unexpectedNode(Set.of(expected), received);
  }

  record MissingNode() implements ConfigError {
    @Override
    public String message() {
      return "value is missing";
    }
  }

  private static String nodeName(Class<? extends ConfigNode> node) {
    if (node == MapNode.class) {
      return "map";
    } else if (node == ValueNode.class) {
      return "value";
    } else if (node == ListNode.class) {
      return "list";
    } else if (node == NullNode.class) {
      return "null";
    }
    return node.getSimpleName();
  }

  record UnexpectedRootPath() implements ConfigError {
    @Override
    public String message() {
      return "expected non-empty path";
    }
  }

  record DuplicateEnumValues(Class<?> type) implements ConfigError {
    @Override
    public String message() {
      return "enum %s has duplicated values".formatted(type.toString());
    }
  }

  record TypedDeserializeErrors(Class<?> type, List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to deserialize " + type.getSimpleName() + System.lineSeparator() + formatErrors(errors);
    }
  }

  record GroupedErrors(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return errors.stream()
          .map(ConfigError::message)
          .reduce((first, second) -> first + "\n- " + second)
          .orElse("");
    }
  }

  record CollectionDeserializeErrors(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to deserialize collection element(s) (" + errors.size() + ")" + System.lineSeparator() + formatErrors(errors);
    }
  }

  record UndefinedParameterType(Class<?> type) implements ConfigError {
    @Override
    public String message() {
      return type.getSimpleName() + " has undefined parameters types";
    }
  }

  record UnexpectedKeyNode(Class<? extends ConfigNode> node) implements ConfigError {
    @Override
    public String message() {
      return "failed to create key from " + ConfigError.nodeName(node);
    }
  }

  record UnknownDeserializer(ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "deserializer for %s not found".formatted(type.clazz().getSimpleName());
    }
  }

  record UnknownSerializer(ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "serializer for %s not found".formatted(type.clazz().getSimpleName());
    }
  }

  record KeyedError(String key, ConfigError error) implements ConfigError {
    public KeyedError(NodeKey key, ConfigError error) {
      this(key.key(), error);
    }

    @Override
    public String message() {
      return "'" + key + "': " + error.message();
    }
  }

  record TypedSerializeErrors(Class<?> type, List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to serialize " + type.getSimpleName() + System.lineSeparator() + formatErrors(errors);
    }
  }

  record CollectionSerializeErrors(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to serialize collection element(s) (" + errors.size() + ")" + System.lineSeparator() + formatErrors(errors);
    }
  }

  record SourceErrors(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to load sources:" + System.lineSeparator() + formatErrors(errors);
    }
  }

  record UnnamedConstructorParameter(ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "constructor parameter has undefined name (%s)".formatted(type.clazz().getSimpleName());
    }
  }

  record ConstructorMissingParameters() implements ConfigError {
    @Override
    public String message() {
      return "missing constructor parameters";
    }
  }

  record InaccessibleConstructor(String message) implements ConfigError {
    @Override
    public String message() {
      return "failed to access constructor: " + message;
    }
  }

  record ObjectInitializationError(String message) implements ConfigError {
    @Override
    public String message() {
      return "failed to initialize: " + message;
    }
  }

  record InaccessibleRecordComponent(String name, ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "failed to access record component %s (%s)".formatted(name, type.clazz().getSimpleName());
    }
  }

  record InaccessibleField(String name, ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "failed to access field '%s' (%s)".formatted(name, type.clazz().getSimpleName());
    }
  }

  record InaccessibleMethod(String name, ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "failed to access method %s (%s)".formatted(name, type.clazz().getSimpleName());
    }
  }

  record NoRecordConstructor() implements ConfigError {
    @Override
    public String message() {
      return "no record constructor";
    }
  }

  record UnknownInitializerProperty(String name, ValueType<?> type) implements ConfigError {
    @Override
    public String message() {
      return "failed to resolve initializer property %s (%s)".formatted(name, type.clazz().getSimpleName());
    }
  }

  record MissingInitializer() implements ConfigError {
    @Override
    public String message() {
      return "no initializers found";
    }
  }

  record MissingNoArgsConstructor() implements ConfigError {
    @Override
    public String message() {
      return "no arguments constructor is missing";
    }
  }

  record ExpectedNonNull() implements ConfigError {
    @Override
    public String message() {
      return "expected value but received null";
    }
  }

  record NotSubclass(Class<?> parent, Class<?> type) implements ConfigError {
    @Override
    public String message() {
      return "%s is not a subclass of %s".formatted(type.getSimpleName(), parent.getSimpleName());
    }
  }

  record InvalidClass(String name) implements ConfigError {
    @Override
    public String message() {
      return "received invalid class: %s".formatted(name);
    }
  }

  record UnknownSubclass(Class<?> clazz) implements ConfigError {
    @Override
    public String message() {
      return "failed to resolve name of %s".formatted(clazz.getSimpleName());
    }
  }

  record UnknownType(String name) implements ConfigError {
    @Override
    public String message() {
      return "unknown type %s".formatted(name);
    }
  }

  record UnknownHierarchyType() implements ConfigError {
    @Override
    public String message() {
      return "failed to find any type information";
    }
  }

  record IncompatibleHierarchyParentNode() implements ConfigError {
    @Override
    public String message() {
      return "failed to attach type info";
    }
  }

  record DuplicateSubclassName(String name, Class<?> firstType, Class<?> secondType) implements ConfigError {
    @Override
    public String message() {
      return "%s refers to 2 different types (%s, %s)".formatted(name, firstType.getSimpleName(), secondType.getSimpleName());
    }
  }

  record DefaultsError(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to parse preUpdates: " + formatErrors(errors);
    }
  }

  record UpdatesError(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to parse config updates: " + formatErrors(errors);
    }
  }

  record OverridesError(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to parse overrides: " + formatErrors(errors);
    }
  }

  record WritingErrors(List<ConfigError> errors) implements ConfigError {
    @Override
    public String message() {
      return "failed to write config:" + System.lineSeparator() + formatErrors(errors);
    }
  }

  record UnknownFile(Path path) implements ConfigError {
    @Override
    public String message() {
      return "no file found %s".formatted(path);
    }
  }

  record SourceLoadingError(String id) implements ConfigError {
    @Override
    public String message() {
      return "failed to load source %s".formatted(id != null ? id : "");
    }
  }

  record UnknownFormat(String format) implements ConfigError {
    @Override
    public String message() {
      return "unknown source format %s".formatted(format);
    }
  }

  private static String formatErrors(List<ConfigError> errors) {
    return errors.stream()
        .map(ConfigError::message)
        .map(error -> ("- " + error).indent(2))
        .collect(Collectors.joining());
  }
}
