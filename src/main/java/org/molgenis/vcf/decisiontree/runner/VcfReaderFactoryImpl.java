package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFIteratorBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.runner.info.GenotypeMetadataMapper;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParser;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataParserFactory;
import org.springframework.stereotype.Component;

@Component
class VcfReaderFactoryImpl implements VcfReaderFactory {

  private final VepMetadataParserFactory vepMetadataParserFactory;
  private final GenotypeMetadataMapper genotypeMetadataMapper;

  VcfReaderFactoryImpl(VepMetadataParserFactory vepMetadataParserFactory,
                       GenotypeMetadataMapper genotypeMetadataMapper) {
    this.vepMetadataParserFactory = requireNonNull(vepMetadataParserFactory);
    this.genotypeMetadataMapper = requireNonNull(genotypeMetadataMapper);
  }

  @Override
  public VcfReader create(Settings settings) {
    VepMetadataParser vepMetadataParser = vepMetadataParserFactory.create(settings);

    Path inputVcfPath = settings.getInputVcfPath();
    boolean strict = settings.isStrict();
      try {
          return new VcfReader(new VCFIteratorBuilder().open(VcfInputStreamDecorator.preprocessVCF(inputVcfPath.toFile())), vepMetadataParser,
              genotypeMetadataMapper,
              strict);
      } catch (IOException e) {
          throw new UncheckedIOException(e);
      }
  }
}
