package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.runner.info.GenotypeMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapperFactory;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  private final GenotypeMetadataMapper genotypeMetadataMapper;
  private final VepMetadataMapperFactory vepMetadataMapperFactory;

  VcfReaderFactoryImpl(VepMetadataMapperFactory vepMetadataMapperFactory,
                       GenotypeMetadataMapper genotypeMetadataMapper) {
    this.vepMetadataMapperFactory = requireNonNull(vepMetadataMapperFactory);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
  }

  @Override
  public VcfReader create(Settings settings) {
    VepMetadataMapper vepMetadataMapper = vepMetadataMapperFactory.create(settings);

    Path inputVcfPath = settings.getInputVcfPath();
    boolean strict = settings.isStrict();
    return new VcfReader(new VCFFileReader(inputVcfPath.toFile(), false), vepMetadataMapper,
        genotypeMetadataMapper,
        strict);
  }
}
