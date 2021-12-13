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
public class MultiQueryNode implements DecisionNode {

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String description;

  @NonNull DecisionType decisionType = DecisionType.BOOL_MULTI;
  @NonNull Field field;
  // @NonFinal and nullable to allow for two-pass construction
  @NonFinal
  @Setter
  Map<BoolQuery, NodeOutcome> outcomeMap;
  @NonFinal
  @Setter
  NodeOutcome outcomeMissing;
  @NonFinal
  @Setter
  NodeOutcome outcomeDefault;
}
