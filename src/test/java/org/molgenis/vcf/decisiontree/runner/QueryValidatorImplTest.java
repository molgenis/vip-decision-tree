package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
   queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
        ConfigOperator.EQUALS).value("test").build(), field);
  }

  @Test
  void validateBooleanNodeFlag() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLAG).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.CONTAINS).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeFloatPass() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.INTEGER).valueCount(
        ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.GREATER).value("test").build(), field);
  }

  @Test
  void validateBooleanNodeStringGreater() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.GREATER).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeFloatIn() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLOAT).valueCount(
        ValueCount.builder().type(Type.FIXED).build()).build();
    assertThrows(
        UnsupportedValueTypeException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.IN).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeContainsFixed1() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.CONTAINS).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeInFixed2() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.IN).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeInA() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.A).build()).build();
    queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.IN).value("test").build(), field);
  }

  @Test
  void validateBooleanNodeInGenotype() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.G).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.IN).value("test").build(), field));
  }

  @Test
  void validateBooleanNodeInVariable() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.VARIABLE).count(2).build()).build();
    assertThrows(
        UnsupportedValueCountTypeException.class,
        () -> queryValidator.validateBooleanNode(ConfigBoolQuery.builder().field("test").operator(
            ConfigOperator.IN).value("test").build(), field));
  }

  @Test
  void validateCategoricalNodeFlag() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.FLAG).valueCount(
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
  void validateCategoricalNumberFixedMorePAss() {
    Field field = Field.builder().id("test").fieldType(FieldType.INFO).valueType(ValueType.STRING).valueCount(
        ValueCount.builder().type(Type.FIXED).count(1).build()).build();
    queryValidator.validateCategoricalNode(field);
  }
}