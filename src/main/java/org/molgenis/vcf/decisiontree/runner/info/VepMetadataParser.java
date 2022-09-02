package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;

public interface VepMetadataParser {

  NestedHeaderLine map(VCFHeader header);
}
