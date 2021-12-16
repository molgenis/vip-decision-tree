package org.molgenis.vcf.decisiontree.filter;

import java.util.Collection;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.Field;

public abstract class AbstractBoolNodeEvaluator<T extends DecisionNode> implements
    NodeEvaluator<T> {

  boolean isMissingValue(Object value) {
    return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
  }

  boolean executeQuery(BoolQuery boolQuery, Object value) {
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
  boolean executeLessQuery(Field field, Object value, Object queryValue) {
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
  boolean executeGreaterQuery(Field field, Object value, Object queryValue) {
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

  boolean executeContainsQuery(Collection<?> values, Object queryValue) {
    return values.contains(queryValue);
  }

  boolean executeContainsAllQuery(Collection<?> values, Collection<?> queryValues) {
    return values.containsAll(queryValues);
  }

  boolean executeContainsAnyQuery(Collection<?> values, Collection<?> queryValues) {
    for (Object queryValue : queryValues) {
      if (values.contains(queryValue)) {
        return true;
      }
    }
    return false;
  }

  boolean executeContainsNoneQuery(Collection<?> values, Collection<?> queryValues) {
    return !executeContainsAnyQuery(values, queryValues);
  }

  boolean executeInQuery(Object value, Collection<?> queryValues) {
    return queryValues.contains(value);
  }
}
