package org.molgenis.vcf.decisiontree.filter;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import org.molgenis.vcf.decisiontree.filter.model.Decision;

public interface DecisionWriter extends AutoCloseable {
  void write(List<Decision> decisions, VariantContext vcfRecord);
}
