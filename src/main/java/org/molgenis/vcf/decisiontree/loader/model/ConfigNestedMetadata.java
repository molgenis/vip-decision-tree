package org.molgenis.vcf.decisiontree.loader.model;

import java.util.List;
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
public class ConfigNestedMetadata {
  @NonNull String separator;
  List<ConfigSelector> unique;
  Map<String, ConfigNestedField> fields;
}
