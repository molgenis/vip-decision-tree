package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public interface VcfNestedMetadataParser {
   VcfNestedMetadata map(VCFHeader header);
}
