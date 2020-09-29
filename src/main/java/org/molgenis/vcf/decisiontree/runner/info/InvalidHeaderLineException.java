package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.String.format;

public class InvalidHeaderLineException extends
    RuntimeException {
  private static final String MESSAGE = "VCF header with id '%s' is not a SnpEff header.";

  public InvalidHeaderLineException(String id) {
    super(format(MESSAGE, id));
  }
}
