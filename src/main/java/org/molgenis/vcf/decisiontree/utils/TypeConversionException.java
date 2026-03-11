package org.molgenis.vcf.decisiontree.utils;

import static java.lang.String.format;

import java.io.Serial;

public class TypeConversionException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public TypeConversionException(Object value, Class<?> thatClass) {
    super(
        format(
            "cannot convert type '%s' to '%s'",
            value.getClass().getSimpleName(), thatClass.getSimpleName()));
  }
}
