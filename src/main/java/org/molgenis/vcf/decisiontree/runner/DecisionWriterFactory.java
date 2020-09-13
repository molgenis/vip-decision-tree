package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;

interface DecisionWriterFactory {

  DecisionWriter create(VcfMetadata vcfMetadata, Settings settings);
}
