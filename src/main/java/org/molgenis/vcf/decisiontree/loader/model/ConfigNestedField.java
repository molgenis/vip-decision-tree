package org.molgenis.vcf.decisiontree.loader.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigNestedField {
  @NonNull int index;
  @NonNull ValueType type;
  @NonNull ValueCount.Type number;
  @NonNull String separator;
  Integer count;
}
