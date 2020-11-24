package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.List;
import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public class VepInfoSelector implements NestedInfoSelector {

  public static final String ALLELE_NUM = "ALLELE_NUM";
  public static final String PICK = "PICK";
  public static final String PREFERRED = "PREFERRED";

  private NestedField alleleNumField;
  private NestedField pickField;
  private NestedField preferredField;

  @Override
  public String select(List<String> infoValues, Allele allele) {
    List<String[]> tokensList =
        infoValues.stream()
            .map(VepInfoSelector::toValues)
            .filter(values -> isAlleleMatch(values, allele))
            .collect(toList());

    if (tokensList.isEmpty()) {
      return null;
    }

    String[] tokens = null;
    if (tokensList.size() == 1) {
      tokens = tokensList.get(0);
    } else {
      if (preferredField != null) {
        tokens = getPreferredTokens(tokensList);
      }
      if (tokens == null && pickField != null) {
        tokens = getPickTokens(tokensList);
      }
      if (tokens == null) {
        tokens = tokensList.get(0);
      }
    }
    return String.join("|", tokens);
  }

  private static String[] toValues(String infoValue) {
    return infoValue.split("\\|");
  }

  private boolean isAlleleMatch(String[] values, Allele allele) {
    String alleleIndexValue = values[alleleNumField.getIndex()];
    return allele.getIndex() == parseInt(alleleIndexValue);
  }

  private String[] getPreferredTokens(List<String[]> tokensList) {
    return tokensList.stream().filter(this::isPreferred).findFirst().orElse(null);
  }

  private boolean isPreferred(String[] values) {
    return "1".equals(values[preferredField.getIndex()]);
  }

  private String[] getPickTokens(List<String[]> tokensList) {
    return tokensList.stream().filter(this::isPick).findFirst().orElse(null);
  }

  private boolean isPick(String[] values) {
    return "1".equals(values[pickField.getIndex()]);
  }

  public void setNestedInfoHeaderLine(NestedInfoHeaderLine nestedInfoHeaderLine) {
    requireNonNull(nestedInfoHeaderLine);
    this.alleleNumField = nestedInfoHeaderLine.getField(ALLELE_NUM);
    if (alleleNumField == null) {
      throw new MissingRequiredNestedValueException("VEP", ALLELE_NUM);
    }
    this.pickField = nestedInfoHeaderLine.getField(PICK);
    this.preferredField = nestedInfoHeaderLine.getField(PREFERRED);
  }
}
