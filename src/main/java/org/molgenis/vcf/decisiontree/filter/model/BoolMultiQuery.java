package org.molgenis.vcf.decisiontree.filter.model;

import java.util.List;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.jspecify.annotations.Nullable;

@Value
@Builder
public class BoolMultiQuery {

  public enum Operator {
    AND,
    OR
  }

  String id;
  List<BoolQuery> queryList;
  @NonFinal @Setter NodeOutcome outcomeTrue;
  @Nullable Operator operator;
}
