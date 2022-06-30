package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.FieldType;

public class MissingPhenotypesException extends
    RuntimeException {

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