package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;

public interface NestedMetadataMapper {

  boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine);

  NestedInfoHeaderLine map(VCFInfoHeaderLine vcfInfoHeaderLine);
}
