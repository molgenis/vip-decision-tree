package org.molgenis.vcf.decisiontree.filter.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.runner.info.NestedValueSelector;

@Data
@Builder
@NonFinal
@AllArgsConstructor
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
  /**
   * Returns index for INFO_NESTED type, null otherwise
   */
  int index;
  /**
   * Returns selectorQueries for INFO_NESTED type, null otherwise
   */
  NestedValueSelector nestedValueSelector;
  /**
   * Returns the parent field
   */
  String parentId;
  /**
   * Returns a map with child fields
   */
  Map<String, Field> children;
}
