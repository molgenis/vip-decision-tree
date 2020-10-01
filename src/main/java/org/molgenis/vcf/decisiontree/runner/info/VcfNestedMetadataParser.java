package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.vcf.VCFHeader;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.Field;

public interface VcfNestedMetadataParser {
   Map<String, Field> map(VCFHeader header);
}
