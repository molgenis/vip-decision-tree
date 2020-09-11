package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class NodeEvaluatorServiceImpl implements NodeEvaluatorService {

  private final BoolNodeEvaluator boolNodeEvaluator;
  private final CategoricalNodeEvaluator categoricalNodeEvaluator;

  NodeEvaluatorServiceImpl() {
    boolNodeEvaluator = new BoolNodeEvaluator();
    categoricalNodeEvaluator = new CategoricalNodeEvaluator();
  }

  // Testability
  NodeEvaluatorServiceImpl(
      BoolNodeEvaluator boolNodeEvaluator, CategoricalNodeEvaluator categoricalNodeEvaluator) {
    this.boolNodeEvaluator = boolNodeEvaluator;
    this.categoricalNodeEvaluator = categoricalNodeEvaluator;
  }

  @Override
  public NodeOutcome evaluate(DecisionNode node, Variant variant) {
    NodeOutcome nodeOutcome;
    DecisionType decisionType = node.getDecisionType();
    switch (decisionType) {
      case BOOL:
        nodeOutcome = boolNodeEvaluator.evaluate((BoolNode) node, variant);
        break;
      case CATEGORICAL:
        nodeOutcome = categoricalNodeEvaluator.evaluate((CategoricalNode) node, variant);
        break;
      default:
        throw new UnexpectedEnumException(decisionType);
    }
    return nodeOutcome;
  }
}
