package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.VcfReader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class AppRunnerFactoryImpl implements AppRunnerFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppRunnerFactoryImpl.class);

  private final VcfReaderFactory vcfReaderFactory;
  private final ClassifierFactory classifierFactory;
  private final DecisionWriterFactory decisionWriterFactory;
  private final DecisionTreeFactory decisionTreeFactory;

  AppRunnerFactoryImpl(
      VcfReaderFactory vcfReaderFactory,
      ClassifierFactory classifierFactory,
      DecisionWriterFactory decisionWriterFactory,
      DecisionTreeFactory decisionTreeFactory) {
    this.vcfReaderFactory = requireNonNull(vcfReaderFactory);
    this.classifierFactory = requireNonNull(classifierFactory);
    this.decisionWriterFactory = requireNonNull(decisionWriterFactory);
    this.decisionTreeFactory = requireNonNull(decisionTreeFactory);
  }

  // Suppress 'Resources should be closed'
  @SuppressWarnings("java:S2095")
  @Override
  public AppRunner create(Settings settings) {
    VcfReader vcfReader = vcfReaderFactory.create(settings);
    try {
      VcfMetadata vcfMetadata = vcfReader.getMetadata();

      Classifier classifier = classifierFactory.create(settings);
      DecisionTree decisionTree = decisionTreeFactory.map(vcfMetadata, settings);
      DecisionWriter decisionWriter = decisionWriterFactory.create(vcfMetadata, settings);
      return new AppRunnerImpl(classifier, vcfReader, decisionTree, decisionWriter);
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
