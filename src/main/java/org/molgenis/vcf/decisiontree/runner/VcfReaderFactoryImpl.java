package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  @Override
  public VCFFileReader create(Settings settings) {
    Path inputVcfPath = settings.getInputVcfPath();
    return new VCFFileReader(inputVcfPath.toFile(), false);
  }
}
