package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.String.format;

import java.io.Serial;

public class MissingRequiredNestedValueException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public MissingRequiredNestedValueException(String fieldName, String nestedFieldName) {
    super(
        format(
            "For '%s' annotations the '%s' field is expected to be present.",
            fieldName, nestedFieldName));
  }
}
