package org.molgenis.vcf.decisiontree.runner.info;

import java.io.Serial;

import static java.lang.String.format;

public class MissingRequiredNestedValueException extends RuntimeException{
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String MESSAGE = "For '%s' annotations the '%s' field is expected to be present.";

  public MissingRequiredNestedValueException(String fieldName, String nestedFieldName) {
    super(format(MESSAGE, fieldName, nestedFieldName));
  }
}
