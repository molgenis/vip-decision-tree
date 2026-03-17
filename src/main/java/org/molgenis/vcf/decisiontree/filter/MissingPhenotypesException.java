package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class MissingPhenotypesException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final String sample;

  public MissingPhenotypesException(String sample) {
    this.sample = sample;
  }

  @Override
  public String getMessage() {
    return format(
        "Attempting to filter on phenotypes without specifying phenotypes for sample '%s'.",
        sample);
  }
}
