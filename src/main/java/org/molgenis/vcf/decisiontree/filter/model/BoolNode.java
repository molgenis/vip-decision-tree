package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
@NonFinal
public class BoolNode implements DecisionNode {
  public static final String FILE_PREFIX = "file:";
  public static final String FIELD_PREFIX = "field:";

  NodeType nodeType = NodeType.DECISION;
  String id;
  String label;
  @Nullable String description;

  DecisionType decisionType = DecisionType.BOOL;
  BoolQuery query;
  // @NonFinal and nullable to allow for two-pass construction
  @Nullable @NonFinal @Setter NodeOutcome outcomeTrue;
  @Nullable @NonFinal @Setter NodeOutcome outcomeFalse;
  @Nullable @NonFinal @Setter NodeOutcome outcomeMissing;
}
