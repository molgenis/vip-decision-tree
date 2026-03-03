package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class MissingPhenotypesException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private static final String MESSAGE =
      "Attempting to filter on phenotypes without specifying phenotypes for smaple '%s'.";
  private final String sample;

  public MissingPhenotypesException(String sample) {
    this.sample = sample;
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, sample);
  }
}
