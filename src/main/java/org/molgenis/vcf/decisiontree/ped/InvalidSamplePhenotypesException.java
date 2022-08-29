package org.molgenis.vcf.decisiontree.ped;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class InvalidSamplePhenotypesException extends RuntimeException {

  private static final String MESSAGE =
      "Invalid phenotype argument: '%s', valid example: 'sample1/phenotype1;phenotype2,sample2/phenotype1'";
  private final String argument;

  public InvalidSamplePhenotypesException(String argument) {
    this.argument = requireNonNull(argument);
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, argument);
  }
}
