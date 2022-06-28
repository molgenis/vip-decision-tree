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
public class ConfigPhenotypeNode implements ConfigNode {

  @NonNull Type type = Type.SAMPLE_PHENOTYPE;

  String description;
  @NonNull ConfigOperator operator;
  @NonNull String field;
  @NonNull ConfigNodeOutcome outcomeTrue;
  @NonNull ConfigNodeOutcome outcomeFalse;
  ConfigNodeOutcome outcomeMissing;
}
