package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class ExistsNodeEvaluator implements NodeEvaluator<ExistsNode> {

  @Override
  public NodeOutcome evaluate(ExistsNode node, Variant variant) {
    NodeOutcome nodeOutcome;
    if (node.getField() instanceof MissingField) {
      nodeOutcome = node.getOutcomeFalse();
    } else {
      Object value = variant.getValue(node.getField());
      boolean matches = value != null;
      nodeOutcome = matches ? node.getOutcomeTrue() : node.getOutcomeFalse();
    }
    return nodeOutcome;
  }

}
