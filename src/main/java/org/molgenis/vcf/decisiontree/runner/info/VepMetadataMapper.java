package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;

public interface VepMetadataMapper {

  boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine);

  VepHeaderLine map(VCFInfoHeaderLine vcfInfoHeaderLine);
}
