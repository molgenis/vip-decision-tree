package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;

interface VcfReaderFactory {

  VCFFileReader create(Settings settings);
}
