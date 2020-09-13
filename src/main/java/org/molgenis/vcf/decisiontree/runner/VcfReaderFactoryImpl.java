package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  @Override
  public VcfReader create(Settings settings) {
    Path inputVcfPath = settings.getInputVcfPath();
    return new VcfReader(new VCFFileReader(inputVcfPath.toFile(), false));
  }
}
