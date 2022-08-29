package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.molgenis.vcf.decisiontree.filter.model.FieldType;

public class UnknownFieldException extends
    RuntimeException {

  private static final String MESSAGE = "Unknown '%s' field '%s'.";
  private final String field;
  private final FieldType type;

  public UnknownFieldException(String field, FieldType type) {
    this.field = requireNonNull(field);
    this.type = requireNonNull(type);
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, type.toString(), field);
  }
}
