package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class CategoricalNode implements DecisionNode {

  @NonNull DecisionType decisionType = DecisionType.CATEGORICAL;
  @NonNull String id;
  @NonNull String label;
  String description;

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull Field field;
  // @NonFinal and nullable to allow for two-pass construction
  @NonFinal
  @Setter
  Map<String, NodeOutcome> outcomeMap;
  @NonFinal
  @Setter
  NodeOutcome outcomeMissing;
  @NonFinal
  @Setter
  NodeOutcome outcomeDefault;
}
