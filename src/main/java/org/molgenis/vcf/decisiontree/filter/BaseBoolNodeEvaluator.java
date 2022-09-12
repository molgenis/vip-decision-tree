package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FIELD_PREFIX;

import java.util.Collection;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.springframework.lang.Nullable;

interface BaseBoolNodeEvaluator<T extends DecisionNode> extends
    NodeEvaluator<T> {

  default boolean isMissingValue(Object value) {
    return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
  }

  default boolean executeQuery(BoolQuery boolQuery, Object value) {
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
  default boolean executeLessQuery(Field field, Object value, Object queryValue) {
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
  default boolean executeGreaterQuery(Field field, Object value, Object queryValue) {
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

  default boolean executeContainsQuery(Collection<?> values, Object queryValue) {
    return values.contains(queryValue);
  }

  default boolean executeContainsAllQuery(Collection<?> values, Collection<?> queryValues) {
    return values.containsAll(queryValues);
  }

  default boolean executeContainsAnyQuery(Collection<?> values, Collection<?> queryValues) {
    for (Object queryValue : queryValues) {
      if (values.contains(queryValue)) {
        return true;
      }
    }
    return false;
  }

  default boolean executeContainsNoneQuery(Collection<?> values, Collection<?> queryValues) {
    return !executeContainsAnyQuery(values, queryValues);
  }

  default boolean executeInQuery(Object value, Collection<?> queryValues) {
    return queryValues.contains(value);
  }

  default BoolQuery postProcessQuery(
      BoolQuery query, Variant variant, @Nullable SampleContext sampleContext) {
    String stringQueryValue = query.getValue().toString();
    if (stringQueryValue.startsWith(FIELD_PREFIX)) {
      String fieldId = stringQueryValue.substring(FIELD_PREFIX.length());
      query = BoolQuery.builder().field(query.getField()).operator(query.getOperator())
          .value(variant.getValue(variant.getVcfMetadata().getField(fieldId), sampleContext))
          .build();
    }
    return query;
  }
}
