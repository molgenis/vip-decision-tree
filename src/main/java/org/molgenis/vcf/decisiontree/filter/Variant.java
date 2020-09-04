package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
@NonFinal
public class Variant {
  @NonNull VCFHeader vcfMetadata;
  @NonNull VariantContext vcfRecord;
  @NonNull int alleleIndex;
}
