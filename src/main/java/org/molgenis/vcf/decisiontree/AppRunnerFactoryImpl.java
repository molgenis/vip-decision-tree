package org.molgenis.vcf.decisiontree;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ClassifierFactory;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeFactory;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.DecisionWriterFactory;
import org.molgenis.vcf.decisiontree.filter.ReaderFactory;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.springframework.stereotype.Component;

@Component
class AppRunnerFactoryImpl implements AppRunnerFactory {

  private final ReaderFactory readerFactory;
  private final ClassifierFactory classifierFactory;
  private final DecisionWriterFactory decisionWriterFactory;
  private final DecisionTreeFactory decisionTreeFactory;

  AppRunnerFactoryImpl(
      ReaderFactory readerFactory,
      ClassifierFactory classifierFactory,
      DecisionWriterFactory decisionWriterFactory,
      DecisionTreeFactory decisionTreeFactory) {
    this.readerFactory = requireNonNull(readerFactory);
    this.classifierFactory = requireNonNull(classifierFactory);
    this.decisionWriterFactory = requireNonNull(decisionWriterFactory);
    this.decisionTreeFactory = requireNonNull(decisionTreeFactory);
  }

  @Override
  public AppRunner create(Settings settings) {
    VCFFileReader vcfFileReader = readerFactory.create(settings);
    Classifier classifier = classifierFactory.create(settings);
    DecisionTree decisionTree = decisionTreeFactory.map(settings.getConfigDecisionTree());
    DecisionWriter decisionWriter = decisionWriterFactory.create(vcfFileReader, settings);
    return new AppRunnerImpl(classifier, vcfFileReader, decisionTree, decisionWriter);
  }
}
