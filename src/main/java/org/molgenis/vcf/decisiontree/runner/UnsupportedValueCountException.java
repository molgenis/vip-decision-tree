package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;

import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;

public class UnsupportedValueCountException extends RuntimeException {

  public UnsupportedValueCountException(Field field, DecisionType decisionType) {
    super(
        format(
            "Unsupported number of values (%d) for field '%s' for decision type '%s'.", field.getValueCount().getCount(), field.getId(), decisionType));
  }
}
