package dev.lacrid.hermes.serialization.deserializer.object;

import dev.lacrid.hermes.serialization.deserializer.object.initializer.ObjectInitializer;

import java.util.List;

record ResolvedInitializer<T>(ObjectInitializer<T> initializer, List<ObjectPropertyDeserializer<?, T>> properties) {
}
