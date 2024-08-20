package org.molgenis.vcf.decisiontree.loader.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigBoolQuery {
  @NonNull @Builder.Default ConfigMultiMode multiMode = ConfigMultiMode.SINGLE;
  @NonNull String field;
  @NonNull ConfigOperator operator;
  @NonNull Object value;
}
