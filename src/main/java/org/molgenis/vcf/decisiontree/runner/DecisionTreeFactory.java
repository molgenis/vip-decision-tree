package org.molgenis.vcf.decisiontree.runner;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

interface DecisionTreeFactory {

  DecisionTree map(VCFFileReader vcfFileReader, Settings settings);
}
