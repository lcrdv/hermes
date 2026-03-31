package dev.lacrid.hermes.serialization.deserializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.error.NodeErrors;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.serialization.SerializationConfig;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

import java.util.HashMap;
import java.util.Map;

public final class EnumDeserializerFactory implements DeserializerFactory<Enum> {
  @Override
  public Either<ConfigError, Deserializer<Enum>> make(ValueType<Enum> type, DeserializerContext context) {
    SerializationConfig config = context.config();
    Map<String, Enum> valueByKey = new HashMap<>();
    Enum<?>[] enumConstants = type.clazz().getEnumConstants();

    for (Enum<?> enumConstant : enumConstants) {
      String key = enumConstant.name();
      valueByKey.put(config.ignoreEnumCase() ? key.toLowerCase() : key, enumConstant);
    }

    if (valueByKey.size() != enumConstants.length) {
      return Either.left(new ConfigError.DuplicateEnumValues(type.clazz()));
    }

    return Either.right(new EnumDeserializer<>(valueByKey, context.errorFactory(type), config.ignoreEnumCase()));
  }

  @Override
  public boolean supports(ValueType<?> type) {
    return type.isEnum();
  }

  private static class EnumDeserializer<T extends Enum<T>> extends ValueNodeDeserializer<T> {
    private final Map<String, T> keyToEnum;
    private final NodeErrors errors;
    private final boolean ignoreCase;

    private EnumDeserializer(Map<String, T> keyToEnum, NodeErrors errors, boolean ignoreCase) {
      this.keyToEnum = keyToEnum;
      this.errors = errors;
      this.ignoreCase = ignoreCase;
    }

    @Override
    protected Either<ConfigError, T> deserializeNode(ValueNode node, T defaultValue) {
      String key = node.readString();
      T result = keyToEnum.get(ignoreCase ? key.toLowerCase() : key);
      return result != null
          ? Either.right(result)
          : Either.left(errors.generic(node, "invalid enum constant"));
    }
  }
}
