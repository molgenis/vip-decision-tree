package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.String.format;

import java.io.Serial;

public class MissingRequiredNestedValueException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private static final String MESSAGE =
      "For '%s' annotations the '%s' field is expected to be present.";

  public MissingRequiredNestedValueException(String fieldName, String nestedFieldName) {
    super(format(MESSAGE, fieldName, nestedFieldName));
  }
}
