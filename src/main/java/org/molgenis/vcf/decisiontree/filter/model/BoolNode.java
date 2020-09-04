package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class BoolNode implements DecisionNode {
  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String description;

  @NonNull DecisionType decisionType = DecisionType.BOOL;
  @NonNull BoolQuery query;
  // @NonFinal and nullable to allow for two-pass construction
  @NonFinal @Setter NodeOutcome outcomeTrue;
  @NonFinal @Setter NodeOutcome outcomeFalse;
  @NonFinal @Setter NodeOutcome outcomeMissing;
}
