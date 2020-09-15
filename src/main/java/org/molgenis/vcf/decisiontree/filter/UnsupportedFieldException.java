package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

public class UnsupportedFieldException extends
    RuntimeException {

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
