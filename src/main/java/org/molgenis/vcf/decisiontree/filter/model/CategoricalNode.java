package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Map;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class CategoricalNode implements DecisionNode {

  DecisionType decisionType = DecisionType.CATEGORICAL;
  String id;
  String label;
  @Nullable String description;

  NodeType nodeType = NodeType.DECISION;
  Field field;
  // @NonFinal and nullable to allow for two-pass construction
  @Nullable @NonFinal @Setter Map<String, NodeOutcome> outcomeMap;
  @Nullable @NonFinal @Setter NodeOutcome outcomeMissing;
  @Nullable @NonFinal @Setter NodeOutcome outcomeDefault;
}
