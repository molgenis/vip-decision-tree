package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

public interface VepMetadataMapper {

  boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine);

  NestedHeaderLine map(String id, VCFHeader vcfHeader);
}
