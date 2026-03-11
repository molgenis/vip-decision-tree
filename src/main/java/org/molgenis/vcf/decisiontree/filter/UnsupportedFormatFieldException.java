package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

import java.io.Serial;

public class UnsupportedFormatFieldException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final Class<?> clazz;

  public UnsupportedFormatFieldException(Class<?> clazz) {
    this.clazz = requireNonNull(clazz);
  }

  @Override
  public String getMessage() {
    return String.format(
        "Custom FORMAT field is of type '%s' instead of String.", clazz.getSimpleName());
  }
}
