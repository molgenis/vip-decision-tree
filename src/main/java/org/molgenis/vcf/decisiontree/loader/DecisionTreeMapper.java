package org.molgenis.vcf.decisiontree.loader;

import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

public interface DecisionTreeMapper {
  DecisionTree map(ConfigDecisionTree configDecisionTree);
}
