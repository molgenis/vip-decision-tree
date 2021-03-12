package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class MissingNode implements DecisionNode {

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String description;

  @NonNull DecisionType decisionType = DecisionType.MISSING;
  @NonFinal
  @Setter
  NodeOutcome outcomeMissing;
}
