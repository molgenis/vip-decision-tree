package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;

public interface DecisionWriterFactory {
  DecisionWriter create(VCFFileReader reader, Settings settings);
}
