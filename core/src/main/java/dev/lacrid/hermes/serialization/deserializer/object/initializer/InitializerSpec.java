package dev.lacrid.hermes.serialization.deserializer.object.initializer;

import java.util.List;

public record InitializerSpec<T>(ObjectInitializer<T> initializer, List<InitializerArgument> arguments) {
}
