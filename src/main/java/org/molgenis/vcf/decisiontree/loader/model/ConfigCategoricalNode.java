package org.molgenis.vcf.decisiontree.loader.model;

import java.util.Map;
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
public class ConfigCategoricalNode implements ConfigNode {
  @NonNull Type type = Type.CATEGORICAL;
  String description;

  @NonNull String field;
  @NonNull Map<String, ConfigNodeOutcome> outcomeMap;
  ConfigNodeOutcome outcomeMissing;
  ConfigNodeOutcome outcomeDefault;
}
