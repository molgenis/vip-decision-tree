package org.molgenis.vcf.decisiontree.filter;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

// TODO replace Variant class with VcfRecord + int args (see TODO in VcfUtils)
@Value
@Builder
@NonFinal
public class Variant {

  @NonNull VcfMetadata vcfMetadata;
  @NonNull VcfRecord vcfRecord;
  @NonNull int alleleIndex;
}
