package org.molgenis.vcf.decisiontree.filter;

public interface Classifier {

  void classify(VcfReader vcfReader);
}
