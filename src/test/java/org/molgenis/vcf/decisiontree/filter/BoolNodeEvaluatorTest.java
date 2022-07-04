package org.molgenis.vcf.decisiontree.filter;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

class BoolNodeEvaluatorTest {

  private BoolNodeEvaluator boolNodeEvaluator;

  @BeforeEach
  void setUp() {
    boolNodeEvaluator = new BoolNodeEvaluator();
  }

  @Test
  void evaluateLessTrueInteger() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.INTEGER);

    Operator operator = Operator.LESS;
    Integer queryValue = 0;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(-1);
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateLessEqualTrueInteger() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.INTEGER);

    Operator operator = Operator.LESS_OR_EQUAL;
    Integer queryValue = 0;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(0);
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateLessFalseFloat() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.FLOAT);

    Operator operator = Operator.LESS;
    Double queryValue = 0.;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(1.23);
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateGreaterFalseInteger() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.INTEGER);

    Operator operator = Operator.GREATER;
    Integer queryValue = 0;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(-1);
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateGreaterTrueFloat() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.FLOAT);

    Operator operator = Operator.GREATER;
    Double queryValue = 0.;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(1.23);
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateGreaterEqualTrueFloat() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.FLOAT);

    Operator operator = Operator.GREATER_OR_EQUAL;
    Double queryValue = 0.;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(0. + 1E-6);
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateInTrueString() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.IN;
    List<String> queryValue = List.of("str0", "str1", "str2");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn("str0");
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateNotInFalseString() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.NOT_IN;
    List<String> queryValue = List.of("str0", "str1", "str2");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn("str0");
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsTrueString() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS;
    String queryValue = "str0";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(List.of("str0", "str1", "str2"));
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateNotContainsFalseString() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.NOT_CONTAINS;
    String queryValue = "str1";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(List.of("str0", "str1", "str2"));
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateEqualsString() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.EQUALS;
    String queryValue = "str1";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn("str1");
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateNotEqualsInteger() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.NOT_EQUALS;
    Integer queryValue = 1;
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(2);
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsAll() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_ALL;
    List<String> queryValue = asList("test1", "test2");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsAllFalse() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_ALL;
    List<String> queryValue = asList("test1", "test4");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsAny() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_ANY;
    List<String> queryValue = asList("test1", "test4");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsAnyFalse() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_ANY;
    List<String> queryValue = asList("test4", "test5");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsNone() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_NONE;
    List<String> queryValue = asList("test4", "test5");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeTrue, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateContainsNoneFalse() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.CONTAINS_NONE;
    List<String> queryValue = asList("test1", "test5");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(asList("test1", "test2", "test3"));
    assertEquals(outcomeFalse, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateMissing() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.NOT_EQUALS;
    String queryValue = "str1";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    assertEquals(outcomeMissing, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateMissingMissing() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);

    Operator operator = Operator.NOT_EQUALS;
    String queryValue = "str1";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .build();

    Variant variant = mock(Variant.class);
    assertThrows(EvaluationException.class, () -> boolNodeEvaluator.evaluate(node, variant,
        null));
  }

  @Test
  void evaluateMissingField() {
    MissingField field = mock(MissingField.class);

    Operator operator = Operator.NOT_EQUALS;
    String queryValue = "str1";
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    assertEquals(outcomeMissing, boolNodeEvaluator.evaluate(node, variant, null));
  }

  @Test
  void evaluateMissingValueCountVariable() {
    FieldImpl field = mock(FieldImpl.class);
    when(field.getValueType()).thenReturn(ValueType.STRING);
    when(field.getValueCount()).thenReturn(ValueCount.builder().type(Type.VARIABLE).build());

    Operator operator = Operator.EQUALS;
    List<String> queryValue = List.of("PASS");
    BoolQuery boolQuery =
        BoolQuery.builder().field(field).operator(operator).value(queryValue).build();
    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    BoolNode node =
        BoolNode.builder()
            .id("bool_node")
            .query(boolQuery)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field, null)).thenReturn(List.of());
    assertEquals(outcomeMissing, boolNodeEvaluator.evaluate(node, variant, null));
  }
}
