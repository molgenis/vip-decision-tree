package org.molgenis.vcf.decisiontree.runner.info;

public class MissingVepException extends
    RuntimeException {

  @Override
  public String getMessage() {
    return "Input VCF is missing required VEP annotation.";
  }

}
