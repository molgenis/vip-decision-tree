package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class Label {
  String id;
  @Nullable String description;
}
