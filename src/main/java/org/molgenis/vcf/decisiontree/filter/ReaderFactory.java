package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;

public interface ReaderFactory {
  VCFFileReader create(Settings settings);
}
