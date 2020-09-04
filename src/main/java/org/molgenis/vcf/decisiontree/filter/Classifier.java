package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.vcf.VCFFileReader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface Classifier {

  void classify(VCFFileReader reader, DecisionTree decisionTree, DecisionWriter decisionWriter);
}
