package org.molgenis.vcf.decisiontree.runner.info;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.springframework.stereotype.Component;

@Component
public class VepInfoSelector implements NestedInfoSelector {

  public static final String ALLELE = "Allele";
  public static final String PICK = "PICK";

  @Override
  public boolean isMatch(String infoValue, VariantContext vc, int alleleIndex, NestedInfoHeaderLine nestedInfoHeaderLine) {
    boolean result = true;
    NestedField pick = nestedInfoHeaderLine.getField(PICK);
    NestedField vepAllele = nestedInfoHeaderLine.getField(ALLELE);
    if (vepAllele == null) {
      throw new MissingRequiredNestedValueException("VEP", ALLELE);
    }
    String[] values = infoValue.split("\\|");
    result = matchAltAllele(vc, alleleIndex, result, vepAllele, values);
    if (result && pick != null) {
      result = "1".equals(values[pick.getIndex()]);
    }
    return result;
  }

  private boolean matchAltAllele(VariantContext vc, int alleleIndex, boolean result,
      NestedField vepAllele, String[] values) {
    //Matching on allele is only needed if more than one ALT is present
    if (vc.getAlternateAlleles().size() > 1) {
      String alt = vc.getAlternateAllele(alleleIndex - 1).getBaseString();
      String ref = vc.getReference().getBaseString();
      if (alt.length() >= ref.length()) {
        result = alt.equals(values[vepAllele.getIndex()]);
      } else {
        if(alt.length() == 1){
          result = "-".equals(values[vepAllele.getIndex()]);
        }else{
          result = alt.substring(1).equals(values[vepAllele.getIndex()]);
        }
      }
    }
    return result;
  }
}
