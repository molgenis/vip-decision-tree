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
  @NonNull int alleleIndex;

  public Object getValue(Field field) {
    return vcfRecord.getValue(field, alleleIndex);
  }

  public String toDisplayString() {
    return String.format(
        "%s -> %s", vcfRecord.toDisplayString(), vcfRecord.getAltAllele(alleleIndex - 1));
  }
}
