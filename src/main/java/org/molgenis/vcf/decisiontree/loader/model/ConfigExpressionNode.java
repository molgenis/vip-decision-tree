package org.molgenis.vcf.decisiontree.loader.model;

import lombok.*;
import org.molgenis.vcf.decisiontree.filter.model.Field;

import java.util.Map;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigExpressionNode implements ConfigNode {
  @NonNull Type type = Type.EXPRESSION;

  String description;

  @NonNull String fields;
  @NonNull ConfigExpression expression;
  @NonNull ConfigNodeOutcome outcomeTrue;
  @NonNull ConfigNodeOutcome outcomeFalse;
  ConfigNodeOutcome outcomeMissing;
}
