package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;

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

  public Object getValue(Field field, SampleContext sampleContext) {
    return vcfRecord.getValue(field, allele, sampleContext);
  }

  public String toDisplayString() {
    return String.format(
        "%s -> %s", vcfRecord.toDisplayString(), allele.getBases());
  }
}
