package org.molgenis.vcf.decisiontree.filter;

import java.util.Collection;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class BoolNodeEvaluator implements NodeEvaluator<BoolNode> {

  @Override
  public NodeOutcome evaluate(BoolNode node, Variant variant) {
    NodeOutcome nodeOutcome;

    BoolQuery query = node.getQuery();
    if (query.getField() instanceof MissingField) {
      if (node.getOutcomeMissing() != null) {
        return node.getOutcomeMissing();
      } else {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }
    Object value = variant.getValue(query.getField());
    if (!isMissingValue(value)) {
      boolean matches = executeQuery(query, value);
      nodeOutcome = matches ? node.getOutcomeTrue() : node.getOutcomeFalse();
    } else {
      nodeOutcome = node.getOutcomeMissing();
      if (nodeOutcome == null) {
        throw new EvaluationException(node, variant, "missing 'missingOutcome'");
      }
    }

    return nodeOutcome;
  }

  private boolean isMissingValue(Object value) {
    return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
  }

  private boolean executeQuery(BoolQuery boolQuery, Object value) {
    boolean matches;

    Field field = boolQuery.getField();
    Operator operator = boolQuery.getOperator();
    Object queryValue = boolQuery.getValue();
    switch (operator) {
      case EQUALS:
        matches = value.equals(queryValue);
        break;
      case NOT_EQUALS:
        matches = !value.equals(queryValue);
        break;
      case LESS:
        matches = executeLessQuery(field, value, queryValue);
        break;
      case LESS_OR_EQUAL:
        matches = !executeGreaterQuery(field, value, queryValue);
        break;
      case GREATER:
        matches = executeGreaterQuery(field, value, queryValue);
        break;
      case GREATER_OR_EQUAL:
        matches = !executeLessQuery(field, value, queryValue);
        break;
      case IN:
        matches = executeInQuery(value, (Collection<?>) queryValue);
        break;
      case NOT_IN:
        matches = !executeInQuery(value, (Collection<?>) queryValue);
        break;
      case CONTAINS:
        matches = executeContainsQuery((Collection<?>) value, queryValue);
        break;
      case NOT_CONTAINS:
        matches = !executeContainsQuery((Collection<?>) value, queryValue);
        break;
      case CONTAINS_ALL:
        matches = executeContainsAllQuery((Collection<?>) value, (Collection<?>) queryValue);
        break;
      case CONTAINS_ANY:
        matches = executeContainsAnyQuery((Collection<?>) value, (Collection<?>) queryValue);
        break;
      case CONTAINS_NONE:
        matches = executeContainsNoneQuery((Collection<?>) value, (Collection<?>) queryValue);
        break;
      default:
        throw new UnexpectedEnumException(operator);
    }

    return matches;
  }

  @SuppressWarnings("DuplicatedCode")
  private boolean executeLessQuery(Field field, Object value, Object queryValue) {
    boolean matches;
    switch (field.getValueType()) {
      case INTEGER:
        matches = ((Integer) value) < ((Integer) queryValue);
        break;
      case FLOAT:
        matches = ((Double) value) < ((Double) queryValue);
        break;
      default:
        throw new UnexpectedEnumException(field.getValueType());
    }
    return matches;
  }

  @SuppressWarnings("DuplicatedCode")
  private boolean executeGreaterQuery(Field field, Object value, Object queryValue) {
    boolean matches;
    switch (field.getValueType()) {
      case INTEGER:
        matches = ((Integer) value) > ((Integer) queryValue);
        break;
      case FLOAT:
        matches = ((Double) value) > ((Double) queryValue);
        break;
      default:
        throw new UnexpectedEnumException(field.getValueType());
    }
    return matches;
  }

  private boolean executeContainsQuery(Collection<?> values, Object queryValue) {
    return values.contains(queryValue);
  }

  private boolean executeContainsAllQuery(Collection<?> values, Collection<?> queryValues) {
    return values.containsAll(queryValues);
  }

  private boolean executeContainsAnyQuery(Collection<?> values, Collection<?> queryValues) {
    for (Object queryValue : queryValues) {
      if (values.contains(queryValue)) {
        return true;
      }
    }
    return false;
  }

  private boolean executeContainsNoneQuery(Collection<?> values, Collection<?> queryValues) {
    return !executeContainsAnyQuery(values, queryValues);
  }

  private boolean executeInQuery(Object value, Collection<?> queryValues) {
    return queryValues.contains(value);
  }
}
