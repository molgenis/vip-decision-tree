package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;

interface VcfReaderFactory {

  VcfReader create(Settings settings);
}
