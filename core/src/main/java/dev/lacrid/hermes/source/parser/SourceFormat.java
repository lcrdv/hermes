package dev.lacrid.hermes.source.parser;

import dev.lacrid.hermes.node.path.PathResolver;

public record SourceFormat(String format, PathResolver pathResolver) {
}
