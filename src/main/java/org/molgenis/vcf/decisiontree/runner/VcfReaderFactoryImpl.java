package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.runner.info.NestedMetadataService;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  private NestedMetadataService nestedMetadataService;

  VcfReaderFactoryImpl(NestedMetadataService nestedMetadataService) {
    this.nestedMetadataService = requireNonNull(nestedMetadataService);
  }

  @Override
  public VcfReader create(Settings settings) {
    Path inputVcfPath = settings.getInputVcfPath();
    return new VcfReader(new VCFFileReader(inputVcfPath.toFile(), false), nestedMetadataService);
  }
}
