package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;

public interface Classifier {

  // TODO remove VCFHeader argument (see TODO in VcfUtils)
  void classify(
      Iterable<VariantContext> records,
      DecisionTree decisionTree,
      DecisionWriter decisionWriter,
      VCFHeader vcfHeader);
}
