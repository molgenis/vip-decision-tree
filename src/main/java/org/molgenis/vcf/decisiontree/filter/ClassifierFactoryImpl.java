package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.Settings;
import org.springframework.stereotype.Component;

@Component
public class ClassifierFactoryImpl implements ClassifierFactory {

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
