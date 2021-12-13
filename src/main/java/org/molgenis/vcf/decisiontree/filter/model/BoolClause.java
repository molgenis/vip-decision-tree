package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@Builder
public class BoolClause {

  public enum Operator {
    AND,
    OR
  }

  @NonNull String id;
  @NonNull List<BoolQuery> queryList;
  @NonFinal
  @Setter
  NodeOutcome outcomeTrue;
  Operator operator;
}
