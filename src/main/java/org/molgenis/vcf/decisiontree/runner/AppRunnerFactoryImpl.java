package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.*;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.filter.model.SamplesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class AppRunnerFactoryImpl implements AppRunnerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppRunnerFactoryImpl.class);

  private final VcfReaderFactory vcfReaderFactory;
  private final ClassifierFactory classifierFactory;
  private final RecordWriterFactory recordWriterFactory;
  private final VipScoreAnnotatorFactory vipScoreAnnotatorFactory;

  AppRunnerFactoryImpl(
      VcfReaderFactory vcfReaderFactory,
      ClassifierFactory classifierFactory,
      RecordWriterFactory recordWriterFactory,
      VipScoreAnnotatorFactory vipScoreAnnotatorFactory) {
    this.vcfReaderFactory = requireNonNull(vcfReaderFactory);
    this.classifierFactory = requireNonNull(classifierFactory);
    this.recordWriterFactory = requireNonNull(recordWriterFactory);
    this.vipScoreAnnotatorFactory = requireNonNull(vipScoreAnnotatorFactory);
  }

  // Suppress 'Resources should be closed'
  @SuppressWarnings("java:S2095")
  @Override
  public AppRunner create(Settings settings) {
    VcfReader vcfReader = vcfReaderFactory.create(settings);
    try {
      VcfMetadata vcfMetadata = vcfReader.getMetadata();
      RecordWriter recordWriter = recordWriterFactory.create(vcfMetadata, settings);
      Classifier classifier;
      if (settings.getMode() == Mode.VARIANT) {
        VipScoreAnnotator vipScoreAnnotator = vipScoreAnnotatorFactory.create(settings);

        classifier = classifierFactory.create(settings,
            recordWriter, vcfMetadata, vipScoreAnnotator);
      } else {
        SampleAnnotator sampleAnnotator = SampleAnnotatorFactory.create(settings);
        SamplesContext samplesContext = SamplesContextFactory.create(settings, vcfMetadata);
        classifier = classifierFactory.create(settings,
            recordWriter, sampleAnnotator, samplesContext);
      }

      return new AppRunnerImpl(classifier, vcfReader, recordWriter);
    } catch (Exception e) {
      try {
        vcfReader.close();
      } catch (Exception closeException) {
        LOGGER.warn("error closing vcf reader", closeException);
      }
      throw e;
    }
  }
}
