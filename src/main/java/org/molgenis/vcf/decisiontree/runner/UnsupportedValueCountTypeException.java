package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;

import java.io.Serial;

public class UnsupportedValueCountTypeException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public UnsupportedValueCountTypeException(Field field, DecisionType decisionType) {
    super(
        format(
            "Unsupported value count type '%s' of field '%s' for decision type '%s'.",
            field.getValueCount().getType(), field.getId(), decisionType));
  }
}
