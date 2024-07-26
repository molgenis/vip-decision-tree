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
    EQUALS_SEQUENCE,
    NOT_EQUALS_SEQUENCE,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    IN,
    NOT_IN,
    CONTAINS,
    NOT_CONTAINS,
    CONTAINS_ALL,
    CONTAINS_ANY,
    CONTAINS_NONE,
    RANGE_OVERLAPS,
    RANGE_ABOVE,
    RANGE_BELOW
  }

  @NonNull Field field;
  @NonNull Operator operator;
  Object value;
  @NonNull @Builder.Default MultiMode multiMode = MultiMode.SINGLE;
}
