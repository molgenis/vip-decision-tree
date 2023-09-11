package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;

import java.io.Serial;

public class UnsupportedValueTypeException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public UnsupportedValueTypeException(Field field, DecisionType decisionType) {
    super(
        format(
            "Unsupported value type '%s' of field '%s' for decision type '%s'.",
            field.getValueType(), field.getId(), decisionType));
  }
}
