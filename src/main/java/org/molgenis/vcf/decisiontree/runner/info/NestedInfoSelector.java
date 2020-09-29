package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public interface NestedInfoSelector {
  boolean isMatch(String infoValue, VariantContext vc, int alleleIndex, NestedInfoHeaderLine nestedInfoHeaderLine);
}
