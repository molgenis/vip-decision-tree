package org.molgenis.vcf.decisiontree.loader;

import java.io.Serial;

public class ConfigDecisionTreeValidationException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public ConfigDecisionTreeValidationException(String message) {
    super(message);
  }
}
