package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import org.molgenis.vcf.decisiontree.filter.model.Field;

public interface NestedMetadataMapper {

  boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine);

  Field map(VCFInfoHeaderLine vcfInfoHeaderLine);
}
