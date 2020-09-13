package org.molgenis.vcf.decisiontree;

import static java.util.Objects.requireNonNull;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ClassifierFactory;
import org.molgenis.vcf.decisiontree.filter.DecisionWriter;
import org.molgenis.vcf.decisiontree.filter.DecisionWriterFactory;
import org.molgenis.vcf.decisiontree.filter.ReaderFactory;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.DecisionTreeMapper;
import org.springframework.stereotype.Component;

@Component
class AppRunnerFactoryImpl implements AppRunnerFactory {

  private final ReaderFactory readerFactory;
  private final ClassifierFactory classifierFactory;
  private final DecisionWriterFactory decisionWriterFactory;
  private final DecisionTreeMapper decisionTreeMapper;

  AppRunnerFactoryImpl(
      ReaderFactory readerFactory,
      ClassifierFactory classifierFactory,
      DecisionWriterFactory decisionWriterFactory,
      DecisionTreeMapper decisionTreeMapper) {
    this.readerFactory = requireNonNull(readerFactory);
    this.classifierFactory = requireNonNull(classifierFactory);
    this.decisionWriterFactory = requireNonNull(decisionWriterFactory);
    this.decisionTreeMapper = requireNonNull(decisionTreeMapper);
  }

  @Override
  public AppRunner create(Settings settings) {
    VCFFileReader vcfFileReader = readerFactory.create(settings);
    Classifier classifier = classifierFactory.create(settings);
    DecisionTree decisionTree = decisionTreeMapper.map(settings.getConfigDecisionTree());
    DecisionWriter decisionWriter = decisionWriterFactory.create(vcfFileReader, settings);
    return new AppRunnerImpl(classifier, vcfFileReader, decisionTree, decisionWriter);
  }
}
