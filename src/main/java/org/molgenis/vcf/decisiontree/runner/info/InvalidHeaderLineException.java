package org.molgenis.vcf.decisiontree.runner.info;

import static java.lang.String.format;

import java.io.Serial;

public class InvalidHeaderLineException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public InvalidHeaderLineException(String id) {
    super(format("VCF header with id '%s' is not a SnpEff header.", id));
  }
}
