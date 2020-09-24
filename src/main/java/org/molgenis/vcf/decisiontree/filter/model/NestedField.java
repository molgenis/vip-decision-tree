package org.molgenis.vcf.decisiontree.filter.model;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Getter
@EqualsAndHashCode
@NonFinal
public class NestedField extends Field {

  @NonNull final int index;
  @NonNull final Field parent;
  @Setter Map<NestedField, Object> selectors;

  // Suppress 'Methods should not have too many parameters'
  @SuppressWarnings("java:S107")
  @Builder(builderMethodName = "nestedBuilder")
  public NestedField(String id,
      FieldType fieldType,
      ValueType valueType,
      ValueCount valueCount,
      Integer count,
      Character separator, int index,
      Map<NestedField, Object> selectors,
      Field parent) {
    super(id,
        fieldType,
        valueType,
        valueCount,
        count,
        separator);
    this.index = requireNonNull(index);
    this.selectors = selectors;
    this.parent = requireNonNull(parent);
  }
}
