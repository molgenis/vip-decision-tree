package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.MissingNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class NodeEvaluatorServiceImpl implements NodeEvaluatorService {

  private final BoolNodeEvaluator boolNodeEvaluator;
  private final CategoricalNodeEvaluator categoricalNodeEvaluator;
  private final ExistsNodeEvaluator existsNodeEvaluator;

  NodeEvaluatorServiceImpl() {
    boolNodeEvaluator = new BoolNodeEvaluator();
    categoricalNodeEvaluator = new CategoricalNodeEvaluator();
    existsNodeEvaluator = new ExistsNodeEvaluator();
  }

  // Testability
  NodeEvaluatorServiceImpl(
      BoolNodeEvaluator boolNodeEvaluator, CategoricalNodeEvaluator categoricalNodeEvaluator,
      ExistsNodeEvaluator existsNodeEvaluator) {
    this.boolNodeEvaluator = boolNodeEvaluator;
    this.categoricalNodeEvaluator = categoricalNodeEvaluator;
    this.existsNodeEvaluator = existsNodeEvaluator;
  }

  @Override
  public NodeOutcome evaluate(DecisionNode node, Variant variant) {
    NodeOutcome nodeOutcome;
    DecisionType decisionType = node.getDecisionType();
    switch (decisionType) {
      case EXISTS:
        nodeOutcome = existsNodeEvaluator.evaluate((ExistsNode) node, variant);
        break;
      case MISSING:
        nodeOutcome = getMissingOutcome((MissingNode) node);
        break;
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

  private NodeOutcome getMissingOutcome(MissingNode node) {
    return node.getOutcomeMissing();
  }
}
