package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;

interface DecisionWriterFactory {

  DecisionWriter create(VCFFileReader reader, Settings settings);
}
