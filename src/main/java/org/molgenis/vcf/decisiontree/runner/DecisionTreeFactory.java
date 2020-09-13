package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

interface DecisionTreeFactory {

  DecisionTree map(VcfMetadata vcfMetadata, Settings settings);
}
