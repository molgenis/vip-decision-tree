package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class ExistsNode implements DecisionNode {

  NodeType nodeType = NodeType.DECISION;
  String id;
  String label;
  Field field;
  @Nullable String description;

  DecisionType decisionType = DecisionType.EXISTS;
  @NonFinal @Setter NodeOutcome outcomeTrue;
  @NonFinal @Setter NodeOutcome outcomeFalse;
}
