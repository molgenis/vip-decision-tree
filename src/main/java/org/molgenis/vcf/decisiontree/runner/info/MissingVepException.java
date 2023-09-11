package org.molgenis.vcf.decisiontree.runner.info;

import java.io.Serial;

public class MissingVepException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  @Override
  public String getMessage() {
    return "Input VCF is missing required VEP annotation.";
  }

}
