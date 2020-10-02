package org.molgenis.vcf.decisiontree.runner.info;

import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.filter.Allele;
import org.molgenis.vcf.decisiontree.filter.model.NestedField;

public class SnpEffInfoSelector implements NestedInfoSelector {

  public static final String ALLELE = "Allele";

  private NestedInfoHeaderLine nestedInfoHeaderLine;

  @Override
  public boolean isMatch(String infoValue, Allele allele) {
    NestedField vepAllele = nestedInfoHeaderLine.getField(ALLELE);
    if (vepAllele == null) {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
    String alt = allele.getBases();
    String[] values = infoValue.split("\\|");
    return alt.equals(values[vepAllele.getIndex()]);
  }

  public void setNestedInfoHeaderLine(NestedInfoHeaderLine nestedInfoHeaderLine) {
    requireNonNull(nestedInfoHeaderLine);
    NestedField vepAllele = nestedInfoHeaderLine.getField(ALLELE);
    if (vepAllele == null) {
      throw new MissingRequiredNestedValueException("SnpEff", ALLELE);
    }
    this.nestedInfoHeaderLine = nestedInfoHeaderLine;
  }
}
