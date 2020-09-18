package org.molgenis.vcf.decisiontree.loader.model;

import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigField {
  @NonNull char separator;
  @NonNull Map<String,ConfigNestedMetadata> fields;
}
