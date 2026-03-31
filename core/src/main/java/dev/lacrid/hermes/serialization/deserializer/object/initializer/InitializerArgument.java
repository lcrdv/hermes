package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import dev.lacrid.hermes.type.ValueType;

public record InitializerArgument(String name, ValueType<?> type) {
}
