package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.utils.QueryUtil;
import org.springframework.stereotype.Component;

@Component
public class BoolNodeEvaluator implements NodeEvaluator<BoolNode> {

  @Override
  public NodeOutcome evaluate(BoolNode node, Variant variant) {
    NodeOutcome nodeOutcome;

    BoolQuery query = node.getQuery();
    Object value = variant.getValue(query.getField());
    if (value != null) {
      boolean matches = QueryUtil.executeQuery(query, value);
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
