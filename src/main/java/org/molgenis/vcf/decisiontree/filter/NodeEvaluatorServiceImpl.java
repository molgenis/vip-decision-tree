package org.molgenis.vcf.decisiontree.filter;

import java.util.List;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;
import org.molgenis.vcf.decisiontree.filter.model.SamplePhenotypeNode;
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
  public NodeOutcome evaluate(DecisionNode node, Variant variant, SampleMeta sampleMeta) {
    NodeOutcome nodeOutcome;
    DecisionType decisionType = node.getDecisionType();
    String sampleName = sampleMeta.getSampleName();
    switch (decisionType) {
      case EXISTS:
        nodeOutcome = existsNodeEvaluator.evaluate((ExistsNode) node, variant, sampleName);
        break;
      case BOOL:
        nodeOutcome = boolNodeEvaluator.evaluate((BoolNode) node, variant, sampleName);
        break;
      case BOOL_MULTI:
        nodeOutcome = boolMultiNodeEvaluator.evaluate((BoolMultiNode) node, variant, sampleName);
        break;
      case CATEGORICAL:
        nodeOutcome = categoricalNodeEvaluator.evaluate((CategoricalNode) node, variant,
            sampleName);
        break;
      case SAMPLE_PHENOTYPE:
        if (sampleName == null) {
          throw new UnsupportedOperationException(
              "Cannot filter on phenotypes when running in variant filter mode.");
        }
        BoolNode boolNode = injectPhenotypes((SamplePhenotypeNode) node,
            sampleMeta.getSamplePhenotypes());
        nodeOutcome = boolNodeEvaluator.evaluate(boolNode, variant, sampleName);
        break;
      default:
        throw new UnexpectedEnumException(decisionType);
    }
    return nodeOutcome;
  }

  private BoolNode injectPhenotypes(SamplePhenotypeNode node, List<String> samplePhenotypes) {
    BoolQuery query = BoolQuery.builder().field(node.getField()).operator(node.getOperator())
        .value(samplePhenotypes).build();
    return BoolNode.builder().id(node.getId()).query(query)
        .outcomeFalse(node.getOutcomeFalse()).outcomeTrue(node.getOutcomeTrue())
        .outcomeMissing(node.getOutcomeMissing()).build();
  }
}
