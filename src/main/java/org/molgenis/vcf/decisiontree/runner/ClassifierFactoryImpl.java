package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.Classifier;
import org.molgenis.vcf.decisiontree.filter.ClassifierImpl;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutor;
import org.springframework.stereotype.Component;

@Component
class ClassifierFactoryImpl implements ClassifierFactory {

  private final DecisionTreeExecutorFactory decisionTreeExecutorFactory;

  ClassifierFactoryImpl(DecisionTreeExecutorFactory decisionTreeExecutorFactory) {
    this.decisionTreeExecutorFactory = requireNonNull(decisionTreeExecutorFactory);
  }

  @Override
  public Classifier create(Settings settings) {
    DecisionTreeExecutor decisionTreeExecutor =
        decisionTreeExecutorFactory.create(settings.getWriterSettings());
    return new ClassifierImpl(decisionTreeExecutor);
  }
}
