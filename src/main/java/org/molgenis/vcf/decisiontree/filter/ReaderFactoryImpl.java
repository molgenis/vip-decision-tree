package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.springframework.stereotype.Component;

@Component
public class ReaderFactoryImpl implements ReaderFactory {
  @Override
  public VCFFileReader create(Settings settings) {
    Path inputVcfPath = settings.getInputVcfPath();
    return new VCFFileReader(inputVcfPath.toFile(), false);
  }
}
