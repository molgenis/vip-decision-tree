package org.molgenis.vcf.decisiontree.runner.info;

public class MissingNestedInfoFieldException extends
    RuntimeException {

  public MissingNestedInfoFieldException(String name) {
    super(String.format("Nested info fields should always have an %s.", name));
  }
}
