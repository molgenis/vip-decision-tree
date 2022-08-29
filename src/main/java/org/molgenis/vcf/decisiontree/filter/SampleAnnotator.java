package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface SampleAnnotator {

  void annotate(List<Decision> decisions, Integer sampleIndex, VariantContextBuilder vc);
}
