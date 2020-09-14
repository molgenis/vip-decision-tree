package org.molgenis.vcf.decisiontree.utils;

import static java.lang.String.format;

public class TypeConversionException extends RuntimeException {

  public TypeConversionException(Object value, Class<?> thatClass) {
    super(
        format(
            "cannot convert type '%s' to '%s'",
            value.getClass().getSimpleName(), thatClass.getSimpleName()));
  }
}
