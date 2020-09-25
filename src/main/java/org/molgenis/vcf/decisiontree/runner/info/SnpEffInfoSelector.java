package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoSelector implements NestedInfoSelector {

  public static final String ALLELE = "Allele";

  @Override
  public boolean isMatch(String infoValue, VariantContext vc, int alleleIndex,
      Map<String, NestedField> nestedFields) {
    NestedField vepAllele = nestedFields.get(ALLELE);
    if (vepAllele == null) {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
    String alt = vc.getAlternateAllele(alleleIndex - 1).getBaseString();
    String[] values = infoValue.split("\\|");
    return alt.equals(values[vepAllele.getIndex()]);
  }
}
