package dev.lacrid.hermes.node.metadata;

record DefaultMetadataKey<T>(String id, Class<T> type) implements MetadataKey<T> {
}
