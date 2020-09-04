package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
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
    NOT_IN
  }

  @NonNull List<String> field;
  @NonNull Operator operator;
  @NonNull Object value;
}
