package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;

public interface NodeEvaluatorService {

  NodeOutcome evaluate(DecisionNode node, Variant variant, SampleContext sampleContext);

  default NodeOutcome evaluate(DecisionNode node, Variant variant) {
    return evaluate(node, variant, null);
  }
}
