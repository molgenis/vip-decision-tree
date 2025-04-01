package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotatorImpl;
import org.molgenis.vcf.decisiontree.runner.info.VepMetadataMapperFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsequenceAnnotatorFactoryImpl implements ConsequenceAnnotatorFactory{

  private final VepMetadataMapperFactory vepMetadataMapperFactory;

  ConsequenceAnnotatorFactoryImpl(VepMetadataMapperFactory vepMetadataMapperFactory) {
      this.vepMetadataMapperFactory = vepMetadataMapperFactory;
  }

  public ConsequenceAnnotator create(Settings settings, VCFHeader header) {
    return new ConsequenceAnnotatorImpl(settings.getWriterSettings().isWriteLabels(),
        settings.getWriterSettings().isWritePath(), header, vepMetadataMapperFactory.create(settings));
  }
}
