package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface Classifier {

  void classify(VcfReader vcfReader, DecisionTree decisionTree, RecordWriter recordWriter,
      ConsequenceAnnotator consequenceAnnotator);
}
