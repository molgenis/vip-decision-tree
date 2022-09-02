package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.springframework.lang.Nullable;

public interface NodeEvaluator<T extends DecisionNode> {

  NodeOutcome evaluate(T node,
      Variant variant, @Nullable SampleContext sampleContext);

  default NodeOutcome evaluate(T node,
      Variant variant) {
    return evaluate(node, variant, null);
  }
}
