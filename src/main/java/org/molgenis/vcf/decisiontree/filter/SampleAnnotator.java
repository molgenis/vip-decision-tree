package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContext;

public interface SampleAnnotator {

  VariantContext annotate(String decision, String sampleName, VariantContext vc);
}
