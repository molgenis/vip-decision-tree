package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

import java.io.Serial;

public class UnsupportedOperatorException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;
  public UnsupportedOperatorException(ConfigOperator operator, Field field,
      DecisionType decisionType) {
    super(
        format(
            "Unsupported operator '%s' for field '%s' for decision type '%s'.", operator,
            field.getId(), decisionType));
  }
}
