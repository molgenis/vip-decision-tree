package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;

public interface NodeEvaluator<T extends DecisionNode> {

  NodeOutcome evaluate(T node,
      Variant variant, String sampleName);

  default NodeOutcome evaluate(T node,
      Variant variant) {
    return evaluate(node, variant, null);
  }
}
