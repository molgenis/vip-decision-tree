package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ClassifierImpl;
import org.molgenis.vcf.decisiontree.filter.ConsequenceAnnotator;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutor;
import org.molgenis.vcf.decisiontree.filter.RecordWriter;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.SampleAnnotatorImpl;
import org.molgenis.vcf.decisiontree.filter.SampleClassifierImpl;
import org.springframework.stereotype.Component;

@Component
class ClassifierFactoryImpl implements ClassifierFactory {

  private final DecisionTreeExecutorFactory decisionTreeExecutorFactory;

  ClassifierFactoryImpl(DecisionTreeExecutorFactory decisionTreeExecutorFactory) {
    this.decisionTreeExecutorFactory = requireNonNull(decisionTreeExecutorFactory);
  }

  @Override
  public Classifier create(Settings settings, DecisionTree decisionTree,
      ConsequenceAnnotator consequenceAnnotator, RecordWriter recordWriter,
      VcfMetadata vcfMetadata) {
    DecisionTreeExecutor decisionTreeExecutor =
        decisionTreeExecutorFactory.create(settings.getWriterSettings());

    return new ClassifierImpl(decisionTreeExecutor, new VepHelper(), decisionTree,
        consequenceAnnotator, recordWriter, vcfMetadata);
  }

  @Override
  public Classifier create(Settings settings, DecisionTree decisionTree,
      RecordWriter recordWriter) {
    DecisionTreeExecutor decisionTreeExecutor =
        decisionTreeExecutorFactory.create(settings.getWriterSettings());

    return new SampleClassifierImpl(decisionTreeExecutor, new VepHelper(), decisionTree,
        recordWriter, settings.getSampleInfo(), new SampleAnnotatorImpl());
  }
}
