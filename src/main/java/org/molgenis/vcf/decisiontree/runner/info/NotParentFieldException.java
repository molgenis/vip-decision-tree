package org.molgenis.vcf.decisiontree.runner.info;

import java.io.Serial;

public class NotParentFieldException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;
  private final String id;

  public NotParentFieldException(String id) {
    this.id = id;
  }

  @Override
  public String getMessage() {
    return String.format("Field '%s' is not a parent field, it has no nested fields.", id);
  }
}
