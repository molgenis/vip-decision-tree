package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class LeafNode implements Node {
  @NonNull NodeType nodeType = NodeType.LEAF;
  @NonNull String id;
  String description;

  @NonNull String clazz;
}
