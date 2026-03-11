package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import java.io.Serial;

public class MissingFileException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public MissingFileException(String file) {
    super(format("Unknown file '%s'.", file));
  }
}
