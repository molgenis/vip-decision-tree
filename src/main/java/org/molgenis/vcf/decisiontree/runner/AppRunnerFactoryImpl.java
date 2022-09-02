package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.SampleAnnotator;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
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
  private final DecisionTreeFactory decisionTreeFactory;

  AppRunnerFactoryImpl(
      VcfReaderFactory vcfReaderFactory,
      ClassifierFactory classifierFactory,
      RecordWriterFactory recordWriterFactory,
      DecisionTreeFactory decisionTreeFactory) {
    this.vcfReaderFactory = requireNonNull(vcfReaderFactory);
    this.classifierFactory = requireNonNull(classifierFactory);
    this.recordWriterFactory = requireNonNull(recordWriterFactory);
    this.decisionTreeFactory = requireNonNull(decisionTreeFactory);
  }

  // Suppress 'Resources should be closed'
  @SuppressWarnings("java:S2095")
  @Override
  public AppRunner create(Settings settings) {
    VcfReader vcfReader = vcfReaderFactory.create(settings);
    try {
      VcfMetadata vcfMetadata = vcfReader.getMetadata();
      RecordWriter recordWriter = recordWriterFactory.create(vcfMetadata, settings);
      DecisionTree decisionTree = decisionTreeFactory.map(vcfMetadata, settings);
      ValueValidator.validate(settings.getConfigDecisionTree(), vcfMetadata);
      Classifier classifier;
      if (settings.getMode() == Mode.VARIANT) {
        ConsequenceAnnotator consequenceAnnotator = ConsequenceAnnotatorFactory.create(settings);
        classifier = classifierFactory.create(settings, decisionTree, consequenceAnnotator,
            recordWriter, vcfMetadata);
      } else {
        SampleAnnotator sampleAnnotator = SampleAnnotatorFactory.create(settings);
        SamplesContext samplesContext = SamplesContextFactory.create(settings, vcfMetadata);
        classifier = classifierFactory.create(settings, decisionTree,
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
