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
public class MissingField implements Field {

  @NonNull String id;

  @Override
  public FieldType getFieldType() {
    throw new UnsupportedOperationException(
        String
            .format("Fieldtype is unavailable for field '%s' that is not present in the input vcf.",
                id));
  }

  @Override
  public ValueType getValueType() {
    throw new UnsupportedOperationException(
        String
            .format("ValueType is unavailable for field '%s' that is not present in the input vcf.",
                id));
  }

  @Override
  public ValueCount getValueCount() {
    throw new UnsupportedOperationException(
        String.format(
            "ValueCount is unavailable for field '%s' that is not present in the input vcf.", id));
  }

  @Override
  public Integer getCount() {
    throw new UnsupportedOperationException(
        String.format("Count is unavailable for field '%s' that is not present in the input vcf.",
            id));
  }

  @Override
  public Character getSeparator() {
    throw new UnsupportedOperationException(
        String
            .format("Separator is unavailable for field '%s' that is not present in the input vcf.",
                id));
  }
}
