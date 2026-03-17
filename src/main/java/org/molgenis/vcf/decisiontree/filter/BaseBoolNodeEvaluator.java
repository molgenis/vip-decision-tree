package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FIELD_PREFIX;

import java.util.Collection;
import org.jspecify.annotations.Nullable;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.SampleContext;
import org.molgenis.vcf.utils.UnexpectedEnumException;

interface BaseBoolNodeEvaluator<T extends DecisionNode> extends NodeEvaluator<T> {

  default boolean executeQuery(BoolQuery boolQuery, Object value) {
    boolean matches;

    Field field = boolQuery.getField();
    Operator operator = boolQuery.getOperator();
    Object queryValue = boolQuery.getValue();

    matches =
        switch (operator) {
          case EQUALS -> value.equals(queryValue);
          case NOT_EQUALS -> !value.equals(queryValue);
          case LESS -> executeLessQuery(field, value, queryValue);
          case LESS_OR_EQUAL -> !executeGreaterQuery(field, value, queryValue);
          case GREATER -> executeGreaterQuery(field, value, queryValue);
          case GREATER_OR_EQUAL -> !executeLessQuery(field, value, queryValue);
          case IN -> executeInQuery(value, (Collection<?>) queryValue);
          case NOT_IN -> !executeInQuery(value, (Collection<?>) queryValue);
          case CONTAINS -> executeContainsQuery((Collection<?>) value, queryValue);
          case NOT_CONTAINS -> !executeContainsQuery((Collection<?>) value, queryValue);
          case CONTAINS_ALL ->
              executeContainsAllQuery((Collection<?>) value, (Collection<?>) queryValue);
          case CONTAINS_ANY ->
              executeContainsAnyQuery((Collection<?>) value, (Collection<?>) queryValue);
          case CONTAINS_NONE ->
              executeContainsNoneQuery((Collection<?>) value, (Collection<?>) queryValue);
        };

    return matches;
  }

  @SuppressWarnings("DuplicatedCode")
  default boolean executeLessQuery(Field field, Object value, Object queryValue) {
    return switch (field.getValueType()) {
      case INTEGER -> ((Integer) value) < ((Integer) queryValue);
      case FLOAT -> ((Double) value) < ((Double) queryValue);
      default -> throw new UnexpectedEnumException(field.getValueType());
    };
  }

  @SuppressWarnings("DuplicatedCode")
  default boolean executeGreaterQuery(Field field, Object value, Object queryValue) {
    return switch (field.getValueType()) {
      case INTEGER -> ((Integer) value) > ((Integer) queryValue);
      case FLOAT -> ((Double) value) > ((Double) queryValue);
      default -> throw new UnexpectedEnumException(field.getValueType());
    };
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
      Object value = variant.getValue(variant.getVcfMetadata().getField(fieldId), sampleContext);
      query =
          BoolQuery.builder()
              .field(query.getField())
              .operator(query.getOperator())
              .value(value != null ? value : "")
              .build();
    }
    return query;
  }
}
