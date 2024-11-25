package org.molgenis.vcf.decisiontree.filter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@Value
@Builder
@NonFinal
@AllArgsConstructor
public class FieldImpl implements Field {

  @NonNull String id;
  @NonNull FieldType fieldType;
  @NonNull
  ValueType valueType;
  @NonNull
  ValueCount valueCount;
  /**
   * Returns count for FIXED value type, null otherwise
   */
  Integer count;
  /**
   * Returns separator for INFO_NESTED type, null otherwise
   */
  Character separator;
}
