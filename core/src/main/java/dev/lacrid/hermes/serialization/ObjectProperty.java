package dev.lacrid.hermes.serialization;

import dev.lacrid.hermes.type.ValueType;

public record ObjectProperty<P, T>(String name, PropertyAccessor<P, T> accessor, ValueType<P> type) {
}
