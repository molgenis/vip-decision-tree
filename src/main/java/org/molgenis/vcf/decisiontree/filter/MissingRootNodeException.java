package org.molgenis.vcf.decisiontree.filter;

import java.io.Serial;

public class MissingRootNodeException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  public MissingRootNodeException() {
    super("The decision tree does not contain a root node.");
  }
}
