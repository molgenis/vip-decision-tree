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
public class ConfigBoolNode implements ConfigNode {
  @NonNull Type type = Type.BOOL;

  String description;
  @NonNull ConfigBoolQuery query;
  @NonNull ConfigNodeOutcome outcomeTrue;
  @NonNull ConfigNodeOutcome outcomeFalse;
  ConfigNodeOutcome outcomeMissing;
}
