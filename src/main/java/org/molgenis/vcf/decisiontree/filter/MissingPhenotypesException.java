package org.molgenis.vcf.decisiontree.filter;

import java.io.Serial;

import static java.lang.String.format;

public class MissingPhenotypesException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String MESSAGE = "Attempting to filter on phenotypes without specifying phenotypes for smaple '%s'.";
  private final String sample;

  public MissingPhenotypesException(String sample) {
    this.sample = sample;
  }

  @Override
  public String getMessage() {
    return format(MESSAGE, sample);
  }
}