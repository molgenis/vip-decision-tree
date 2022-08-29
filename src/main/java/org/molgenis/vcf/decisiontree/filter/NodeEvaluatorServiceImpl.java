package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class NodeEvaluatorServiceImpl implements NodeEvaluatorService {

  private final BoolNodeEvaluator boolNodeEvaluator;
  private final CategoricalNodeEvaluator categoricalNodeEvaluator;
  private final ExistsNodeEvaluator existsNodeEvaluator;
  private final BoolMultiNodeEvaluator boolMultiNodeEvaluator;

  NodeEvaluatorServiceImpl() {
    boolNodeEvaluator = new BoolNodeEvaluator();
    boolMultiNodeEvaluator = new BoolMultiNodeEvaluator();
    categoricalNodeEvaluator = new CategoricalNodeEvaluator();
    existsNodeEvaluator = new ExistsNodeEvaluator();
  }

  // Testability
  NodeEvaluatorServiceImpl(
      BoolNodeEvaluator boolNodeEvaluator, BoolMultiNodeEvaluator boolMultiNodeEvaluator,
      CategoricalNodeEvaluator categoricalNodeEvaluator,
      ExistsNodeEvaluator existsNodeEvaluator) {
    this.boolNodeEvaluator = boolNodeEvaluator;
    this.boolMultiNodeEvaluator = boolMultiNodeEvaluator;
    this.categoricalNodeEvaluator = categoricalNodeEvaluator;
    this.existsNodeEvaluator = existsNodeEvaluator;
  }

  @Override
  public NodeOutcome evaluate(DecisionNode node, Variant variant,
      @Nullable SampleContext sampleContext) {
    NodeOutcome nodeOutcome;
    DecisionType decisionType = node.getDecisionType();
    switch (decisionType) {
      case EXISTS:
        nodeOutcome = existsNodeEvaluator.evaluate((ExistsNode) node, variant, sampleContext);
        break;
      case BOOL:
        nodeOutcome = boolNodeEvaluator.evaluate((BoolNode) node, variant, sampleContext);
        break;
      case BOOL_MULTI:
        nodeOutcome = boolMultiNodeEvaluator.evaluate((BoolMultiNode) node, variant, sampleContext);
        break;
      case CATEGORICAL:
        nodeOutcome = categoricalNodeEvaluator.evaluate((CategoricalNode) node, variant
            , sampleContext);
        break;
      default:
        throw new UnexpectedEnumException(decisionType);
    }
    return nodeOutcome;
  }
}
