package org.molgenis.vcf.decisiontree.runner;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.WriterSettings;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutor;
import org.molgenis.vcf.decisiontree.filter.DecisionTreeExecutorImpl;
import org.molgenis.vcf.decisiontree.filter.NodeEvaluatorService;
import org.springframework.stereotype.Component;

@Component
class DecisionTreeExecutorFactoryImpl implements DecisionTreeExecutorFactory {

  private final NodeEvaluatorService nodeEvaluatorService;

  DecisionTreeExecutorFactoryImpl(NodeEvaluatorService nodeEvaluatorService) {
    this.nodeEvaluatorService = requireNonNull(nodeEvaluatorService);
  }

  @Override
  public DecisionTreeExecutor create(WriterSettings writerSettings) {
    return new DecisionTreeExecutorImpl(
        nodeEvaluatorService, writerSettings.isWriteLabels(), writerSettings.isWritePath());
  }
}
