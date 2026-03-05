package org.molgenis.vcf.decisiontree.runner.info;

public class NotParentFieldException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private static final String MESSAGE =
      "Field '%s' is not a parent field, it has no nested fields.";
  private final String id;

  public NotParentFieldException(String id) {
    this.id = id;
  }

  @Override
  public String getMessage() {
    return String.format(MESSAGE, id);
  }
}
