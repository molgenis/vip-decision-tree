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
public class ConfigExistsNode implements ConfigNode {

  @NonNull Type type = Type.EXISTS;
  @NonNull String field;
  String description;
  @NonNull String label;
  @NonNull ConfigNodeOutcome outcomeTrue;
  @NonNull ConfigNodeOutcome outcomeFalse;
}
