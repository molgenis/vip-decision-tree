package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.filter.model.Field;

@Value
@Builder
@NonFinal
public class Variant {

  @NonNull VcfMetadata vcfMetadata;
  @NonNull VcfRecord vcfRecord;
  @NonNull Allele allele;

  public Object getValue(Field field) {
    return vcfRecord.getValue(field, allele, null);
  }

  public Object getValue(Field field, Integer sampleIndex) {
    return vcfRecord.getValue(field, allele, sampleIndex);
  }

  public String toDisplayString() {
    return String.format(
        "%s -> %s", vcfRecord.toDisplayString(), allele.getBases());
  }
}
