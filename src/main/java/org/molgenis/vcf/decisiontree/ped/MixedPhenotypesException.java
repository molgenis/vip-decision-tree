package org.molgenis.vcf.decisiontree.ped;

public class MixedPhenotypesException extends IllegalArgumentException {

  private static final String MESSAGE =
      "Mixing general phenotypes for all samples and phenotypes per sample is not allowed.";

  public MixedPhenotypesException() {
    super(MESSAGE);
  }
}
