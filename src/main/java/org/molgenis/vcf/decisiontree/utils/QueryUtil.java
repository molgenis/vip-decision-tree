package org.molgenis.vcf.decisiontree.utils;

import java.util.List;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.Field;

public class QueryUtil {

  private QueryUtil() {
  }

  public static boolean executeQuery(BoolQuery boolQuery, Object value) {
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
        matches = executeInQuery(value, (List<?>) queryValue);
        break;
      case NOT_IN:
        matches = !executeInQuery(value, (List<?>) queryValue);
        break;
      case CONTAINS:
        matches = executeContainsQuery((List<?>) value, queryValue);
        break;
      case NOT_CONTAINS:
        matches = !executeContainsQuery((List<?>) value, queryValue);
        break;
      default:
        throw new UnexpectedEnumException(operator);
    }

    return matches;
  }

  @SuppressWarnings("DuplicatedCode")
  static boolean executeLessQuery(Field field, Object value, Object queryValue) {
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
  static boolean executeGreaterQuery(Field field, Object value, Object queryValue) {
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

  static boolean executeContainsQuery(List<?> values, Object queryValue) {
    return values.contains(queryValue);
  }

  static boolean executeInQuery(Object value, List<?> queryValues) {
    return queryValues.contains(value);
  }
}