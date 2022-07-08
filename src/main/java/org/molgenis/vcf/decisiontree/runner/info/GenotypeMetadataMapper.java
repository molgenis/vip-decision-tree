package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;

public interface GenotypeMetadataMapper {

  NestedHeaderLine map();
}
