package org.molgenis.vcf.decisiontree.loader.model;

import lombok.*;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigExpression {
  @NonNull String expression;
  @NonNull ConfigOperator operator;
  @NonNull Object value;
}
