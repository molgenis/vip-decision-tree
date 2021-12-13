package org.molgenis.vcf.decisiontree.loader.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConfigBoolClause {

  String id;
  List<ConfigBoolQuery> queries;
  ConfigNodeOutcome outcomeTrue;
  ConfigClauseOperator operator = null;

  public ConfigBoolClause() {
    this.id = UUID.randomUUID().toString();
  }

  //For testability
  public ConfigBoolClause(String id,
      List<ConfigBoolQuery> queries,
      ConfigNodeOutcome outcomeTrue,
      ConfigClauseOperator operator) {
    this.id = id;
    this.queries = queries;
    this.outcomeTrue = outcomeTrue;
    this.operator = operator;
  }

  public String getId() {
    return id;
  }

  public List<ConfigBoolQuery> getQueries() {
    return queries;
  }

  public void setQueries(
      List<ConfigBoolQuery> queries) {
    this.queries = queries;
  }

  public ConfigNodeOutcome getOutcomeTrue() {
    return outcomeTrue;
  }

  public void setOutcomeTrue(ConfigNodeOutcome outcomeTrue) {
    this.outcomeTrue = outcomeTrue;
  }

  public ConfigClauseOperator getOperator() {
    return operator;
  }

  public void setOperator(ConfigClauseOperator operator) {
    this.operator = operator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfigBoolClause that = (ConfigBoolClause) o;
    return queries.equals(that.queries) &&
        outcomeTrue.equals(that.outcomeTrue) &&
        operator == that.operator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(queries, outcomeTrue, operator);
  }
}
