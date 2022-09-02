package org.molgenis.vcf.decisiontree;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class UnsupportedPedException extends RuntimeException {

  private static final String MESSAGE =
      "Phenotype value '%s' that is not an affection status (-9, 0, 1 or 2) is unsupported";
  private final String token;

  public UnsupportedPedException(String token) {
    this.token = requireNonNull(token);
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, token);
  }
}
