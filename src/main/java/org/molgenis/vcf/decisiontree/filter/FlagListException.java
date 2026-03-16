package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class FlagListException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final String field;

  public FlagListException(String field) {
    this.field = field;
  }

  @Override
  public String getMessage() {
    return format("INFO field '%s' of type FLAG cannot contain a list of values.", field);
  }
}
