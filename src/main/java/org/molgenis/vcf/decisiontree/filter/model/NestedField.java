package org.molgenis.vcf.decisiontree.filter.model;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.runner.info.NestedInfoSelector;

@Value
@NonFinal
public class NestedField extends Field {

  @NonNull final int index;
  @NonNull final Field parent;
  @Setter NestedInfoSelector nestedInfoSelector;

  // Suppress 'Methods should not have too many parameters'
  @SuppressWarnings("java:S107")
  @Builder(builderMethodName = "nestedBuilder")
  public NestedField(String id,
      FieldType fieldType,
      ValueType valueType,
      ValueCount valueCount,
      Integer count,
      Character separator, int index,
      NestedInfoSelector nestedInfoSelector,
      Field parent) {
    super(id,
        fieldType,
        valueType,
        valueCount,
        count,
        separator);
    this.index = requireNonNull(index);
    this.nestedInfoSelector = nestedInfoSelector;
    this.parent = requireNonNull(parent);
  }
}
