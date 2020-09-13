package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface DecisionTreeFactory {

  DecisionTree map(VCFFileReader vcfFileReader, Settings settings);
}
