package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;

@Value
@Builder
@NonFinal
public class Variant {

  VcfMetadata vcfMetadata;
  VcfRecord vcfRecord;
  Allele allele;

  public @Nullable Object getValue(Field field) {
    return vcfRecord.getValue(field, allele, null);
  }

  public @Nullable Object getValue(Field field, @Nullable SampleContext sampleContext) {
    return vcfRecord.getValue(field, allele, sampleContext);
  }

  public String toDisplayString() {
    return String.format("%s -> %s", vcfRecord.toDisplayString(), allele.getBases());
  }
}
