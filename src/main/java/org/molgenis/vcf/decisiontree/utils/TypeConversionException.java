package org.molgenis.vcf.decisiontree.utils;

import java.io.Serial;

import static java.lang.String.format;

public class TypeConversionException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public TypeConversionException(Object value, Class<?> thatClass) {
    super(
        format(
            "cannot convert type '%s' to '%s'",
            value.getClass().getSimpleName(), thatClass.getSimpleName()));
  }
}
