package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class LeafNode implements Node {
  NodeType nodeType = NodeType.LEAF;
  String id;
  String label;
  @Nullable String description;

  String clazz;
}
