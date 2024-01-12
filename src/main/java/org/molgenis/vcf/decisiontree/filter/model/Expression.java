package org.molgenis.vcf.decisiontree.filter.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class Expression {
  public enum Operator {
    EQUALS,
    NOT_EQUALS,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL
  }
  @NonNull String calculation;
  @NonNull Map<String, Field> fields;

  Operator operator;
  Double value;
}
