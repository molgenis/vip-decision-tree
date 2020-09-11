package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
@AllArgsConstructor
public class Decision {
  @NonNull String clazz;
  @NonNull List<Node> path;
  @NonNull Set<Label> labels;
}
