package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

public class FlagListException extends RuntimeException {

  private static final String MESSAGE = "INFO field '%s' of type FLAG cannot contain a list of values.";
  private final String field;

  public FlagListException(String field) {
    this.field = field;
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, field);
  }
}
