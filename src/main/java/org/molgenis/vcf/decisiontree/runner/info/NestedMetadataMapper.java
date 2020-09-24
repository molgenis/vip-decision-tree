package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public interface NestedMetadataMapper {

  boolean canMap(VCFInfoHeaderLine vcfInfoHeaderLine);

  Map<String, NestedField> map(VCFInfoHeaderLine vcfInfoHeaderLine);
}
