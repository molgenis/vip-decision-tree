package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;

public class UnsupportedFormatFieldException extends
    RuntimeException {

  private final Class<?> clazz;

  public UnsupportedFormatFieldException(Class<?> clazz) {
    this.clazz = requireNonNull(clazz);
  }

  @Override
  public String getMessage() {
    return String.format("Custom FORMAT field is of type '%s' instead of String.",
        clazz.getSimpleName());
  }
}
