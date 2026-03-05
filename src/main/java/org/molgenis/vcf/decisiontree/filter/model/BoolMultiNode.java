package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class BoolMultiNode implements DecisionNode {

  NodeType nodeType = NodeType.DECISION;
  String id;
  String label;
  @Nullable String description;

  DecisionType decisionType = DecisionType.BOOL_MULTI;
  List<Field> fields;
  @NonFinal @Setter List<BoolMultiQuery> clauses;

  @NonFinal @Setter NodeOutcome outcomeMissing;
  @NonFinal @Setter NodeOutcome outcomeDefault;
}
