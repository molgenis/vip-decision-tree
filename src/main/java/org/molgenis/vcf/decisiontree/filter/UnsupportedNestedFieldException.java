package org.molgenis.vcf.decisiontree.filter;

import java.io.Serial;

public class UnsupportedNestedFieldException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  private final String field;

  public UnsupportedNestedFieldException(String field) {
    this.field = field;
  }

  @Override
  public String getMessage() {
    return String.format("Unsupported parent field '%s', only VEP nested fields are supported.",
        field);
  }
}
