package dev.lacrid.hermes.serialization.serializer;

import dev.lacrid.hermes.error.ConfigError;
import dev.lacrid.hermes.node.ConfigNode;
import dev.lacrid.hermes.node.ValueNode;
import dev.lacrid.hermes.type.ValueType;
import dev.lacrid.lambda.Either;

public class EnumSerializer<T extends Enum<T>> implements Serializer<T> {
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static final SerializerFactory<Enum<?>> FACTORY = SerializerFactory.forType(
      ValueType::isEnum,
      (type, context) -> (Serializer) new EnumSerializer<>(context.config().ignoreEnumCase())
  );

  private final boolean ignoreCase;

  public EnumSerializer(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  @Override
  public Either<ConfigError, ConfigNode> serialize(T value) {
    String valueName = value.name();
    return Either.right(ValueNode.of(ignoreCase ? valueName.toLowerCase() : valueName));
  }
}
