package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class DecisionTree {
  Node rootNode;
}
