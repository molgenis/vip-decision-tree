package org.molgenis.vcf.decisiontree.filter;

public class UnsupportedNestedFieldException extends
    RuntimeException {

  private final String field;

  public UnsupportedNestedFieldException(String field) {
    this.field = field;
  }

  @Override
  public String getMessage() {
    return String.format("Unsupported parent field '%s', only VEP nested fields are supported.",
        field);
  }
}
