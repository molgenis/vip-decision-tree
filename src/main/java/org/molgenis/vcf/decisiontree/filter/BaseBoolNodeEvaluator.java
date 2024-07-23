package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FIELD_PREFIX;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import htsjdk.samtools.cram.digest.ContentDigests;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.springframework.lang.Nullable;

interface BaseBoolNodeEvaluator<T extends DecisionNode> extends
    NodeEvaluator<T> {
  default boolean isMissingValue(Object value) {
    return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
  }

  default boolean executeQuery(BoolQuery boolQuery, Object value) {
    boolean matches;

    Mode gtMode = null;
    Field field = boolQuery.getField();
    Operator operator = boolQuery.getOperator();
    Object queryValue = boolQuery.getValue();

    switch (operator) {
      case EQUALS:
        matches = value.equals(queryValue);
        break;
      case EQUALS_SEQUENCE:
        matches = executeSequenceEqualsQuery(field, value, queryValue);
        break;
      case NOT_EQUALS_SEQUENCE:
        matches = !executeSequenceEqualsQuery(field, value, queryValue);
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

  default boolean executeSequenceEqualsQuery(Field field, Object value, Object queryValue){
    if (field.getValueType() != ValueType.STRING){
      throw new UnexpectedEnumException(field.getValueType());
    }
    String actualValue = value.toString();
    String requestedValue = queryValue.toString();
    if(actualValue.length() != requestedValue.length()){
      return false;
    }

    String shiftedValue = "";
    for(int i = 0; i < actualValue.length(); i++)
    {
      if(i!=0) {
        shiftedValue = shift(shiftedValue);
      }else{
        shiftedValue = actualValue;
      }
      if (sequenceMatch(shiftedValue, requestedValue)){
        return true;
      }
    }
    return false;
  }

  private static boolean sequenceMatch(String actualValue, String requestedValue) {
    for (int i = 0; i < actualValue.length(); i++) {
      if(!isIupacMatch(requestedValue.charAt(i),actualValue.charAt(i))){
        return false;
      }
    }
    return true;
  }

  private static boolean isIupacMatch(Character iupac, Character base){
    //NOT SUPPORTED:
    //B	=> G or T or C	not-A, B follows A
    //V	=> 	G or C or A	not-T (not-U), V follows U
    //D	=> 	G or A or T	not-C, D follows C
    if(iupac.equals('B')||iupac.equals('B')||iupac.equals('B')){
      throw new UnsupportedOperationException(String.format("IUPAC value '%s' is not supported.", iupac));
    }
    HashMap<Character, Set<Character>> iupacMap = new HashMap<>();
    iupacMap.put('G',Set.of('G'));
    iupacMap.put('A',Set.of('A'));
    iupacMap.put('T',Set.of('T'));
    iupacMap.put('C',Set.of('C'));
    iupacMap.put('R',Set.of('G','A'));
    iupacMap.put('Y',Set.of('T','C'));
    iupacMap.put('M',Set.of('A','C'));
    iupacMap.put('K',Set.of('G','T'));
    iupacMap.put('S',Set.of('G','C'));
    iupacMap.put('W',Set.of('A','T'));
    iupacMap.put('H',Set.of('A','C', 'T'));
    iupacMap.put('N',Set.of('G','A','T','C'));

    Set<Character> values = iupacMap.get(iupac);
    if(values.isEmpty()){
      throw new UnsupportedOperationException(String.format("'%s' is not a valid IUPAC base.", iupac));
    }
    return values.contains(base);
  }

  private String shift(String sequence){
    char first = sequence.charAt(0);
    return sequence.substring(1) + first;
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
