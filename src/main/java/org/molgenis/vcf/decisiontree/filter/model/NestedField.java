package org.molgenis.vcf.decisiontree.filter.model;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@Value
@NonFinal
@EqualsAndHashCode(callSuper = false)
public class NestedField extends FieldImpl implements Comparable<NestedField> {

  final int index;
  @NonNull
  final Field parent;

  // Suppress 'Methods should not have too many parameters'
  @SuppressWarnings("java:S107")
  @Builder(builderMethodName = "nestedBuilder")
  public NestedField(String id,
      FieldType fieldType,
      ValueType valueType,
      ValueCount valueCount,
      Integer count,
      Character separator, int index,
      Field parent) {
    super(id,
        fieldType,
        valueType,
        valueCount,
        count,
        separator);
    this.index = requireNonNull(index);
    this.parent = requireNonNull(parent);
  }

  @Override
  public int compareTo(NestedField o) {
    return Integer.valueOf(getIndex()).compareTo(o.getIndex());
  }
}
