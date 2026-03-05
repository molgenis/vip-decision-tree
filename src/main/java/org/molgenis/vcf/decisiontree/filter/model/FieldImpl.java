package org.molgenis.vcf.decisiontree.filter.model;

import lombok.*;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@Value
@Builder
@NonFinal
@AllArgsConstructor
@EqualsAndHashCode
public class FieldImpl implements Field {

  String id;
  FieldType fieldType;
  ValueType valueType;
  ValueCount valueCount;

  /** Returns count for FIXED value type, null otherwise */
  @Nullable Integer count;

  /** Returns separator for INFO_NESTED type, null otherwise */
  @Nullable Character separator;
}
