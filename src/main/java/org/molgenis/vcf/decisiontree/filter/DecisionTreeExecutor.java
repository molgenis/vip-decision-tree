package org.molgenis.vcf.decisiontree.filter;

import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;

public interface DecisionTreeExecutor {

  default Decision execute(DecisionTree tree, Variant variant) {
    return execute(tree, variant, null);
  }

  Decision execute(DecisionTree tree, Variant variant, @Nullable SampleContext sampleContext);
}
