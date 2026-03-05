package org.molgenis.vcf.decisiontree.filter;

import static java.lang.String.format;

import java.io.Serial;

public class UnknownNodeException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public UnknownNodeException(String node) {
    super(format("Unknown node '%s'.", node));
  }
}
