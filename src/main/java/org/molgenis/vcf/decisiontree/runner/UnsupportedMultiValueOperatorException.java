package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

public class UnsupportedMultiValueOperatorException extends RuntimeException {

  public UnsupportedMultiValueOperatorException(Field field, ConfigOperator operator) {
    super(
        format(
            "Cannot use operator '%s' on multi value field '%s'.",
            operator.toString(), field.getId()));
  }
}
