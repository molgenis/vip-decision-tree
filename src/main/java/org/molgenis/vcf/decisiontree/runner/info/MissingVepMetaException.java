package org.molgenis.vcf.decisiontree.runner.info;

import java.io.Serial;

public class MissingVepMetaException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  @Override
  public String getMessage() {
    return "Metadata json is missing required VEP annotation.";
  }

}
