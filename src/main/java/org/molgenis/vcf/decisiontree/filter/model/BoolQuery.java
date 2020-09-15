package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class BoolQuery {
  public enum Operator {
    EQUALS,
    NOT_EQUALS,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    IN,
    NOT_IN,
    CONTAINS,
    NOT_CONTAINS
  }

  @NonNull Field field;
  @NonNull Operator operator;
  @NonNull Object value;
}
