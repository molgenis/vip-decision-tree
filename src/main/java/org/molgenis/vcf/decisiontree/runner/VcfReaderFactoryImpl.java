package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  private VepMetadataParser vepMetadataParser;

  VcfReaderFactoryImpl(VepMetadataParser vepMetadataParser) {
    this.vepMetadataParser = requireNonNull(vepMetadataParser);
  }

  @Override
  public VcfReader create(Settings settings) {
    Path inputVcfPath = settings.getInputVcfPath();
    boolean strict = settings.isStrict();
    return new VcfReader(new VCFFileReader(inputVcfPath.toFile(), false), vepMetadataParser,
        strict);
  }
}
