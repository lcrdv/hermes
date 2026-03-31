package dev.lacrid.hermes.node.metadata;

public interface MetadataKey<T> {
  String id();

  Class<T> type();

  MetadataKey<Boolean> IS_HIDDEN = create("is_hidden", boolean.class); // annotation or ${hidden:valxd}
  MetadataKey<String> SOURCE = create("source", String.class);

  static <T> MetadataKey<T> create(String id, Class<T> type) {
    return new DefaultMetadataKey<>(id, type);
  }
}
