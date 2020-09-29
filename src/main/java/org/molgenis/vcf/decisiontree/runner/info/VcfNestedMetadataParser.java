package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;

public interface VcfNestedMetadataParser {
   VcfNestedMetadata map(VCFHeader header);
}
