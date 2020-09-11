package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.WriterSettings;
import org.springframework.stereotype.Component;

@Component
public class DecisionTreeExecutorFactoryImpl implements DecisionTreeExecutorFactory {

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
