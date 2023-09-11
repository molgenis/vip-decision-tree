package org.molgenis.vcf.decisiontree.runner.info;

import java.io.Serial;

import static java.lang.String.format;

public class InvalidHeaderLineException extends
    RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String MESSAGE = "VCF header with id '%s' is not a SnpEff header.";

  public InvalidHeaderLineException(String id) {
    super(format(MESSAGE, id));
  }
}
