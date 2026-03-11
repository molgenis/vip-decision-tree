package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class UnknownSampleException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public UnknownSampleException(String sampleId) {
    super(format("Unknown sample '%s'.", sampleId));
  }
}
