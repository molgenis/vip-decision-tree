package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.String.format;

public class MissingRequiredNestedValueException extends RuntimeException{
  private static final String MESSAGE = "For '%s' annotations the '%s' field is expected to be present.";

  public MissingRequiredNestedValueException(String fieldName, String nestedFieldName) {
    super(format(MESSAGE, fieldName, nestedFieldName));
  }
}
