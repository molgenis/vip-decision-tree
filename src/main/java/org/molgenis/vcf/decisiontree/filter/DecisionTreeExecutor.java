package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface DecisionTreeExecutor {
  Decision execute(DecisionTree tree, Variant variant, String sampleName);
}
