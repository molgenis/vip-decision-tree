package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class ExistsNode implements DecisionNode {

  @NonNull NodeType nodeType = NodeType.EXISTS;
  @NonNull String id;
  @NonNull Field field;
  String description;

  @NonNull DecisionType decisionType = DecisionType.EXISTS;
  @NonFinal
  @Setter
  NodeOutcome outcomeTrue;
  @NonFinal
  @Setter
  NodeOutcome outcomeFalse;
}
