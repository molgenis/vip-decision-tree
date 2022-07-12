package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;

public class CategoricalNodeEvaluator implements NodeEvaluator<CategoricalNode> {

  @Override
  public NodeOutcome evaluate(CategoricalNode node, Variant variant, SampleContext sampleContext) {
    NodeOutcome nodeOutcome;
    if (node.getField() instanceof MissingField) {
      if (node.getOutcomeMissing() != null) {
        return node.getOutcomeMissing();
      } else {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }
    Object value = variant.getValue(node.getField(), sampleContext);
    if (value != null) {
      nodeOutcome = node.getOutcomeMap().get(value);
      if (nodeOutcome == null) {
        nodeOutcome = node.getOutcomeDefault();
        if (nodeOutcome == null) {
          throw new EvaluationException(node, variant, "missing 'defaultOutcome'");
        }
      }
    } else {
      nodeOutcome = node.getOutcomeMissing();
      if (nodeOutcome == null) {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }

    return nodeOutcome;
  }
}
