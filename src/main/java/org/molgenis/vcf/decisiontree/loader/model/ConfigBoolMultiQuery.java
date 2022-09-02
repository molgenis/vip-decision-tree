package org.molgenis.vcf.decisiontree.loader.model;

import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ConfigBoolMultiQuery {

  String id = UUID.randomUUID().toString();
  List<ConfigBoolQuery> queries;
  ConfigNodeOutcome outcomeTrue;
  ConfigClauseOperator operator;
}
