package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public class VepInfoSelector implements NestedInfoSelector {

  public static final String ALLELE_NUM = "ALLELE_NUM";
  public static final String PICK = "PICK";

  private NestedInfoHeaderLine nestedInfoHeaderLine;

  @Override
  public boolean isMatch(String infoValue, Allele allele) {
    NestedField pickField = nestedInfoHeaderLine.getField(PICK);
    NestedField alleleNum = nestedInfoHeaderLine.getField(ALLELE_NUM);
    String[] values = infoValue.split("\\|");
    boolean isAlleleMatch = (allele.getIndex() == Integer.parseInt(values[alleleNum.getIndex()]));
    boolean isPickMatch = true;
    if (pickField != null) {
      isPickMatch = "1".equals(values[pickField.getIndex()]);
    }
    return isAlleleMatch && isPickMatch;
  }

  public void setNestedInfoHeaderLine(NestedInfoHeaderLine nestedInfoHeaderLine) {
    requireNonNull(nestedInfoHeaderLine);
    NestedField alleleNum = nestedInfoHeaderLine.getField(ALLELE_NUM);
    if (alleleNum == null) {
      throw new MissingRequiredNestedValueException("VEP", ALLELE_NUM);
    }
    this.nestedInfoHeaderLine = nestedInfoHeaderLine;
  }
}
