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
public class Config {
  @NonNull ConfigDecisionTree decisionTree;
  @NonNull Map<String, ConfigNestedMetadata> nestedMetadata;
}
