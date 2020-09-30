package org.molgenis.vcf.decisiontree.runner.info;

import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;
import org.springframework.stereotype.Component;

@Component
public class SnpEffInfoSelector implements NestedInfoSelector {

  public static final String ALLELE = "Allele";

  @Override
  public boolean isMatch(
      String infoValue, Allele allele, NestedInfoHeaderLine nestedInfoHeaderLine) {
    NestedField vepAllele = nestedInfoHeaderLine.getField(ALLELE);
    if (vepAllele == null) {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
    String alt = allele.getBases();
    String[] values = infoValue.split("\\|");
    return alt.equals(values[vepAllele.getIndex()]);
  }
}
