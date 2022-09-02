package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;

interface RecordWriterFactory {

  RecordWriter create(VcfMetadata vcfMetadata, Settings settings);
}
