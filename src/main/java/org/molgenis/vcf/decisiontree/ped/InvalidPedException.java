package org.molgenis.vcf.decisiontree.ped;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class InvalidPedException extends RuntimeException {

  private static final String MESSAGE = "Invalid PED line, expected 6 columns on line: %s";
  private final String argument;

  public InvalidPedException(String argument) {
    this.argument = requireNonNull(argument);
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, argument);
  }
}
