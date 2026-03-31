package dev.lacrid.hermes.tree.processor;

import dev.lacrid.hermes.processor.ProcessorPipeline;
import dev.lacrid.hermes.processor.SourceProcessor;

import java.util.*;

public class TreeProcessors {
  private final Map<ProcessorTag, ProcessorPipeline> pipelines = new HashMap<>();

  public TreeProcessors(List<TaggedProcessor> processors) {
    Map<ProcessorTag, List<SourceProcessor>> processorsByTag = new HashMap<>();

    for (TaggedProcessor processor : processors) {
      processorsByTag.computeIfAbsent(processor.tag(), k -> new ArrayList<>())
          .add(processor.processor());
    }

    processorsByTag.forEach((tag, proc) -> pipelines.put(tag, new ProcessorPipeline(proc)));
  }

  public ProcessorPipeline byTag(ProcessorTag tag) {
    return pipelines.getOrDefault(tag, ProcessorPipeline.EMPTY);
  }
}
