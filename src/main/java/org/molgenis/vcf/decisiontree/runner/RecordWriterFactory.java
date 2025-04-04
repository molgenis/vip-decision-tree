package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;

interface RecordWriterFactory {
  RecordWriter create(VCFHeader vcfHeader, Settings settings);
}
