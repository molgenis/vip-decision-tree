package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class Field {

  @NonNull String id;
  @NonNull FieldType fieldType;
  @NonNull ValueType valueType;
  @NonNull ValueCount valueCount;
  /**
   * Returns count for FIXED value type, null otherwise
   */
  Integer count;
  /**
   * Returns separator for INFO_NESTED type, null otherwise
   */
  Character separator;
}
