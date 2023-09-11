package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

import java.io.Serial;

public class FileValueNotAllowedException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public FileValueNotAllowedException(ConfigOperator operator, String allowedFileOperators,
      String field) {
    super(
        format(
            "Illegal value for field '%s': file values (file:) are not allowed for operator '%s', allowed operators '%s'.",
            field, operator, allowedFileOperators));
  }
}
