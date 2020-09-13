package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;

public interface DecisionTreeFactory {

  DecisionTree map(ConfigDecisionTree configDecisionTree);
}
