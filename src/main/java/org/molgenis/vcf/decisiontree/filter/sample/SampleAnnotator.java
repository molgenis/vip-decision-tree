package org.molgenis.vcf.decisiontree.filter.sample;

import htsjdk.variant.variantcontext.VariantContext;

public interface SampleAnnotator {

  VariantContext annotate(String decision, String sampleName, VariantContext vc);
}
