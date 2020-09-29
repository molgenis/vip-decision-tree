package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.variantcontext.VariantContext;

public interface NestedInfoSelector {
  boolean isMatch(String infoValue, VariantContext vc, int alleleIndex, NestedInfoHeaderLine nestedInfoHeaderLine);
}
