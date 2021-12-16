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
public class BoolMultiNode implements DecisionNode {

  @NonNull NodeType nodeType = NodeType.DECISION;
  @NonNull String id;
  String description;

  @NonNull DecisionType decisionType = DecisionType.BOOL_MULTI;
  @NonNull List<Field> fields;
  @NonNull
  @NonFinal
  @Setter
  List<BoolMultiQuery> clauses;

  @NonFinal
  @Setter
  NodeOutcome outcomeMissing;
  @NonFinal
  @Setter
  NodeOutcome outcomeDefault;
}
