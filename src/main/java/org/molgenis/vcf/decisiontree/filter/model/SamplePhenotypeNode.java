package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class SamplePhenotypeNode implements DecisionNode {

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String description;

  @NonNull DecisionType decisionType = DecisionType.SAMPLE_PHENOTYPE;
  @NonNull BoolQuery.Operator operator;
  @NonNull Field field;
  // @NonFinal and nullable to allow for two-pass construction
  @NonFinal
  @Setter
  NodeOutcome outcomeTrue;
  @NonFinal
  @Setter
  NodeOutcome outcomeFalse;
  @NonFinal
  @Setter
  NodeOutcome outcomeMissing;
}
