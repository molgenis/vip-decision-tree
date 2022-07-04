package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;

public interface NodeEvaluatorService {

  NodeOutcome evaluate(DecisionNode node, Variant variant, Integer sampleIndex);

  default NodeOutcome evaluate(DecisionNode node, Variant variant) {
    return evaluate(node, variant, null);
  }
}
