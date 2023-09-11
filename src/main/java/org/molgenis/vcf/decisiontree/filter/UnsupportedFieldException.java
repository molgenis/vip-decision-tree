package org.molgenis.vcf.decisiontree.filter;

import java.io.Serial;

import static java.lang.String.format;

public class UnsupportedFieldException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String MESSAGE = "Unsupported field '%s'.";
  private final String rootField;

  public UnsupportedFieldException(String rootField) {
    this.rootField = rootField;
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, rootField);
  }
}
