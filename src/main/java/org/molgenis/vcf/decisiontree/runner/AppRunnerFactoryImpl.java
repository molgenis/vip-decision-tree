package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.springframework.stereotype.Component;

@Component
class AppRunnerFactoryImpl implements AppRunnerFactory {

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

  @Override
  public AppRunner create(Settings settings) {
    VCFFileReader vcfFileReader = vcfReaderFactory.create(settings);
    try {
      Classifier classifier = classifierFactory.create(settings);
      DecisionTree decisionTree = decisionTreeFactory.map(vcfFileReader, settings);
      DecisionWriter decisionWriter = decisionWriterFactory.create(vcfFileReader, settings);
      return new AppRunnerImpl(classifier, vcfFileReader, decisionTree, decisionWriter);
    } catch (Exception e) {
      vcfFileReader.close();
      throw e;
    }
  }
}
