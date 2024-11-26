package org.molgenis.vcf.decisiontree.filter.model;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;

@Value
@NonFinal
public class GenotypeField extends FieldImpl implements Comparable<GenotypeField> {

  @NonNull
  final GenotypeFieldType genotypeFieldType;
  @NonNull
  final Field parent;

  // Suppress 'Methods should not have too many parameters'
  @SuppressWarnings("java:S107")
  @Builder(builderMethodName = "nestedBuilder")
  public GenotypeField(String id,
      FieldType fieldType,
      ValueType valueType,
      ValueCount valueCount,
      Integer count,
      Character separator, GenotypeFieldType genotypeFieldType,
      Field parent) {
    super(id,
        fieldType,
        valueType,
        valueCount,
        count,
        separator);
    this.genotypeFieldType = requireNonNull(genotypeFieldType);
    this.parent = requireNonNull(parent);
  }

  @Override
  public int compareTo(GenotypeField o) {
    return getGenotypeFieldType().compareTo(o.getGenotypeFieldType());
  }
}
