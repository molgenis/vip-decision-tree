package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class NodeEvaluatorServiceImpl implements NodeEvaluatorService {

  private final BoolNodeEvaluator boolNodeEvaluator;
  private final CategoricalNodeEvaluator categoricalNodeEvaluator;
  private final ExistsNodeEvaluator existsNodeEvaluator;
  private final BoolMultiNodeEvaluator boolMultiNodeEvaluator;

  private final ExpressionNodeEvaluator expressionNodeEvaluator;
  NodeEvaluatorServiceImpl() {

    expressionNodeEvaluator = new ExpressionNodeEvaluator();
    boolNodeEvaluator = new BoolNodeEvaluator();
    boolMultiNodeEvaluator = new BoolMultiNodeEvaluator();
    categoricalNodeEvaluator = new CategoricalNodeEvaluator();
    existsNodeEvaluator = new ExistsNodeEvaluator();
  }

  // Testability
  NodeEvaluatorServiceImpl(
          BoolNodeEvaluator boolNodeEvaluator, BoolMultiNodeEvaluator boolMultiNodeEvaluator,
          CategoricalNodeEvaluator categoricalNodeEvaluator,
          ExistsNodeEvaluator existsNodeEvaluator, ExpressionNodeEvaluator expressionNodeEvaluator) {
    this.boolNodeEvaluator = boolNodeEvaluator;
    this.boolMultiNodeEvaluator = boolMultiNodeEvaluator;
    this.categoricalNodeEvaluator = categoricalNodeEvaluator;
    this.existsNodeEvaluator = existsNodeEvaluator;
    this.expressionNodeEvaluator = expressionNodeEvaluator;
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
      case EXPRESSION:
        nodeOutcome = expressionNodeEvaluator.evaluate((ExpressionNode) node, variant, sampleContext);
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
