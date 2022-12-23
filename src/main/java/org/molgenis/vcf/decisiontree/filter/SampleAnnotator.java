package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContextBuilder;

public interface SampleAnnotator {

  void annotate(Integer sampleIndex, VariantContextBuilder vc);
}
