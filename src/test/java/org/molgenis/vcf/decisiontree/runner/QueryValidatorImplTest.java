package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.IN;

import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

class QueryValidatorImplTest {

  QueryValidator queryValidator = new QueryValidatorImpl();

  @Test
  void validateBooleanNodeFlagPass() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLAG).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
   validateBooleanNode(field, ConfigOperator.EQUALS);
  }

  @Test
  void validateBooleanNodeFlag() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLAG).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> validateBooleanNode(field, ConfigOperator.CONTAINS));
  }

  @Test
  void validateBooleanNodeEqualsOnCollection() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).separator('&').valueCount(
        ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    assertThrows(
        CountMismatchException.class,
        () -> validateBooleanNode(field, ConfigOperator.NOT_EQUALS));
  }

  @Test
  void validateBooleanNodeFloatPass() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.INTEGER).valueCount(
        ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    validateBooleanNode(field, ConfigOperator.GREATER);
  }

  @Test
  void validateBooleanNodeStringGreater() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> validateBooleanNode(field, ConfigOperator.GREATER));
  }

  @Test
  void validateBooleanNodeFloatIn() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> validateBooleanNode(field, IN));
  }

  @Test
  void validateBooleanNodeContainsFixed1() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> validateBooleanNode(field, ConfigOperator.CONTAINS));
  }

  @Test
  void validateBooleanNodeContainsMultiFixed1() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> validateBooleanNode(field, ConfigOperator.CONTAINS_ALL));
  }

  @Test
  void validateBooleanNodeInFixed2() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> validateBooleanNode(field, IN));
  }

  @Test
  void validateBooleanNodeInA() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.A).build()).build();
    validateBooleanNode(field, IN);
  }

  @Test
  void validateBooleanNodeInGenotype() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.G).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> validateBooleanNode(field, IN));
  }

  @Test
  void validateBooleanNodeInVariable() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.VARIABLE).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> validateBooleanNode(field, IN));
  }

  @Test
  void validateBooleanNodeInFile() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
        IN).value("file:test").build(), field);
  }

  @Test
  void validateBooleanNodeEqualsFile() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    ConfigBoolQuery query = ConfigBoolQuery.builder().field("test").operator(
        ConfigOperator.EQUALS).value("file:test").build();
    assertThrows(
        FileValueNotAllowedException.class,
        () -> queryValidator.validateBooleanNode(query, field));
  }

  @Test
  void validateCategoricalNodeFlag() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLAG)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalCountVariable() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.VARIABLE).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalCountGenotype() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.G).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalNodeFloat() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalNumberGenotype() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.G).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalNumberVariable() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.VARIABLE).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalNumberFixedMoreThan1() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> queryValidator.validateCategoricalNode(field));
  }

  @Test
  void validateCategoricalNumberFixedMorePass() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING)
        .valueCount(
            ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    queryValidator.validateCategoricalNode(field);
  }

  private void validateBooleanNode(Field field, ConfigOperator operator) {
    queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
        operator).value("test").build(), field);
  }
}