package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class UnsupportedFieldException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final String rootField;

  public UnsupportedFieldException(String rootField) {
    this.rootField = rootField;
  }

  @Override
  public String getMessage() {
    return format("Unsupported field '%s'.", rootField);
  }
}
