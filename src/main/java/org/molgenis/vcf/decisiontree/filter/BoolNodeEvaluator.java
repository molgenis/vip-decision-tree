package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.springframework.stereotype.Component;

@Component
public class BoolNodeEvaluator implements BaseBoolNodeEvaluator<BoolNode> {

  @Override
  public NodeOutcome evaluate(BoolNode node,
      Variant variant, SampleContext sampleContext) {
    NodeOutcome nodeOutcome;

    BoolQuery query = postProcessQuery(node.getQuery(), variant, sampleContext);
    if (query.getField() instanceof MissingField) {
      if (node.getOutcomeMissing() != null) {
        return node.getOutcomeMissing();
      } else {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }
    Object value = variant.getValue(query.getField(), sampleContext);
    if (!isMissingValue(value)) {
      boolean matches = executeQuery(query, value);
      nodeOutcome = matches ? node.getOutcomeTrue() : node.getOutcomeFalse();
    } else {
      nodeOutcome = node.getOutcomeMissing();
      if (nodeOutcome == null) {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }

    return nodeOutcome;
  }

}
