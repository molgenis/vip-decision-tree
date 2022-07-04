package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface SampleAnnotator {

  VariantContext annotate(List<Decision> decisions, String sampleName, VariantContext vc);
}
