package org.molgenis.vcf.decisiontree.filter;

import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
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
      BoolNodeEvaluator boolNodeEvaluator,
      BoolMultiNodeEvaluator boolMultiNodeEvaluator,
      CategoricalNodeEvaluator categoricalNodeEvaluator,
      ExistsNodeEvaluator existsNodeEvaluator) {
    this.boolNodeEvaluator = boolNodeEvaluator;
    this.boolMultiNodeEvaluator = boolMultiNodeEvaluator;
    this.categoricalNodeEvaluator = categoricalNodeEvaluator;
    this.existsNodeEvaluator = existsNodeEvaluator;
  }

  @Override
  public NodeOutcome evaluate(
      DecisionNode node, Variant variant, @Nullable SampleContext sampleContext) {
    NodeOutcome nodeOutcome;
    DecisionType decisionType = node.getDecisionType();
    nodeOutcome =
        switch (decisionType) {
          case EXISTS -> existsNodeEvaluator.evaluate((ExistsNode) node, variant, sampleContext);
          case BOOL -> boolNodeEvaluator.evaluate((BoolNode) node, variant, sampleContext);
          case BOOL_MULTI ->
              boolMultiNodeEvaluator.evaluate((BoolMultiNode) node, variant, sampleContext);
          case CATEGORICAL ->
              categoricalNodeEvaluator.evaluate((CategoricalNode) node, variant, sampleContext);
        };
    return nodeOutcome;
  }
}
