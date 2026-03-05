package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
@AllArgsConstructor
public class Decision {
  String clazz;
  List<Node> path;
  Set<Label> labels;
}
