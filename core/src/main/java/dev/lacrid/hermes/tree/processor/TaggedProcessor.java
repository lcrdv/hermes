package dev.lacrid.hermes.tree.processor;

import dev.lacrid.hermes.processor.SourceProcessor;

public record TaggedProcessor(SourceProcessor processor, ProcessorTag tag) {
}
