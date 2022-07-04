package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.springframework.lang.Nullable;

public interface DecisionTreeExecutor {

  default Decision execute(DecisionTree tree, Variant variant) {
    return execute(tree, variant, null);
  }

  Decision execute(DecisionTree tree, Variant variant, @Nullable String sampleName);
}
