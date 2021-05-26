package org.molgenis.vcf.decisiontree.runner.info;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public class SnpEffInfoSelector implements NestedInfoSelector {

  public static final String ALLELE = "Allele";

  private NestedField alleleIndexField;

  @Override
  public String select(List<String> infoValues, Allele allele) {
    String[] tokens =
        infoValues.stream()
            .map(SnpEffInfoSelector::toValues)
            .filter(infoValue -> isMatch(infoValue, allele))
            .findFirst()
            .orElse(null);
    return tokens != null ? String.join("|", tokens) : null;
  }

  private static String[] toValues(String infoValue) {
    return infoValue.split("\\|");
  }

  private boolean isMatch(String[] tokens, Allele allele) {
    String alt = allele.getBases();
    return alt.equals(tokens[alleleIndexField.getIndex()]);
  }

  public void setNestedInfoHeaderLine(NestedInfoHeaderLine nestedInfoHeaderLine) {
    alleleIndexField = nestedInfoHeaderLine.getField(ALLELE);
    if (alleleIndexField == null) {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
  }
}
