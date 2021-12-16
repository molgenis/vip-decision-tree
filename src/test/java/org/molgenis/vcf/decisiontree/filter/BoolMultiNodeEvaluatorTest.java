package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

class BoolMultiNodeEvaluatorTest {

  private BoolMultiNodeEvaluator boolMultiNodeEvaluator;

  @BeforeEach
  void setUp() {
    boolMultiNodeEvaluator = new BoolMultiNodeEvaluator();
  }

  @Test
  void evaluate() {
    FieldImpl field1 = mock(FieldImpl.class);
    FieldImpl field2 = mock(FieldImpl.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);
    when(field2.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQueryAnd1 =
        BoolQuery.builder().field(field1).operator(Operator.LESS).value(1).build();
    BoolQuery boolQueryAnd2 =
        BoolQuery.builder().field(field2).operator(Operator.GREATER).value(1).build();
    BoolQuery boolQueryOr1 =
        BoolQuery.builder().field(field1).operator(Operator.EQUALS).value(2).build();
    BoolQuery boolQueryOr2 =
        BoolQuery.builder().field(field2).operator(Operator.NOT_EQUALS).value(2).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");
    NodeOutcome outcome2 = mock(NodeOutcome.class, "outcome2");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.AND)
        .queryList(List.of(boolQueryAnd1, boolQueryAnd2)).outcomeTrue(outcome1).build();
    BoolMultiQuery boolMultiQuery2 = BoolMultiQuery.builder().id("124")
        .operator(BoolMultiQuery.Operator.OR)
        .queryList(List.of(boolQueryOr1, boolQueryOr2)).outcomeTrue(outcome2).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1, boolMultiQuery2);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1, field2))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(2);
    when(variant.getValue(field2)).thenReturn(2);
    assertEquals(outcome2, boolMultiNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateFalse() {
    FieldImpl field1 = mock(FieldImpl.class);
    FieldImpl field2 = mock(FieldImpl.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);
    when(field2.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQueryAnd1 =
        BoolQuery.builder().field(field1).operator(Operator.LESS).value(1).build();
    BoolQuery boolQueryAnd2 =
        BoolQuery.builder().field(field2).operator(Operator.GREATER).value(1).build();
    BoolQuery boolQueryOr1 =
        BoolQuery.builder().field(field1).operator(Operator.EQUALS).value(2).build();
    BoolQuery boolQueryOr2 =
        BoolQuery.builder().field(field2).operator(Operator.NOT_EQUALS).value(2).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");
    NodeOutcome outcome2 = mock(NodeOutcome.class, "outcome2");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.AND)
        .queryList(List.of(boolQueryAnd1, boolQueryAnd2)).outcomeTrue(outcome1).build();
    BoolMultiQuery boolMultiQuery2 = BoolMultiQuery.builder().id("124")
        .operator(BoolMultiQuery.Operator.AND)
        .queryList(List.of(boolQueryOr1, boolQueryOr2)).outcomeTrue(outcome2).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1, boolMultiQuery2);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1, field2))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(2);
    when(variant.getValue(field2)).thenReturn(2);
    assertEquals(outcomeDefault, boolMultiNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateMissing() {
    Field field1 = mock(FieldImpl.class);
    Field field2 = mock(MissingField.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);
    when(field2.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQueryAnd1 =
        BoolQuery.builder().field(field1).operator(Operator.LESS).value(1).build();
    BoolQuery boolQueryAnd2 =
        BoolQuery.builder().field(field2).operator(Operator.GREATER).value(1).build();
    BoolQuery boolQueryAnd1b =
        BoolQuery.builder().field(field1).operator(Operator.EQUALS).value(2).build();
    BoolQuery boolQueryAnd2b =
        BoolQuery.builder().field(field2).operator(Operator.NOT_EQUALS).value(2).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");
    NodeOutcome outcome2 = mock(NodeOutcome.class, "outcome2");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.AND)
        .queryList(List.of(boolQueryAnd1, boolQueryAnd2)).outcomeTrue(outcome1).build();
    BoolMultiQuery boolMultiQuery2 = BoolMultiQuery.builder().id("124")
        .operator(BoolMultiQuery.Operator.AND)
        .queryList(List.of(boolQueryAnd1b, boolQueryAnd2b)).outcomeTrue(outcome2).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1, boolMultiQuery2);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1, field2))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(2);
    when(variant.getValue(field2)).thenReturn(2);
    assertEquals(outcomeMissing, boolMultiNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateOr() {
    FieldImpl field1 = mock(FieldImpl.class);
    FieldImpl field2 = mock(FieldImpl.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);
    when(field2.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQueryOr1 =
        BoolQuery.builder().field(field1).operator(Operator.LESS).value(1).build();
    BoolQuery boolQueryOr2 =
        BoolQuery.builder().field(field2).operator(Operator.GREATER).value(1).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.OR)
        .queryList(List.of(boolQueryOr1, boolQueryOr2)).outcomeTrue(outcome1).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1, field2))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(2);
    when(variant.getValue(field2)).thenReturn(2);
    assertEquals(outcome1, boolMultiNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateSingleQuery() {
    FieldImpl field1 = mock(FieldImpl.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQuery =
        BoolQuery.builder().field(field1).operator(Operator.GREATER).value(1).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.OR)
        .queryList(List.of(boolQuery)).outcomeTrue(outcome1).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(2);
    assertEquals(outcome1, boolMultiNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateSingleQueryMissingValue() {
    FieldImpl field1 = mock(FieldImpl.class);
    when(field1.getValueType()).thenReturn(ValueType.INTEGER);

    BoolQuery boolQuery =
        BoolQuery.builder().field(field1).operator(Operator.LESS).value(1).build();
    NodeOutcome outcomeDefault = mock(NodeOutcome.class, "outcomeDefault");
    NodeOutcome outcomeMissing = mock(NodeOutcome.class, "outcomeMissing");
    NodeOutcome outcome1 = mock(NodeOutcome.class, "outcome1");

    BoolMultiQuery boolMultiQuery1 = BoolMultiQuery.builder().id("123")
        .operator(BoolMultiQuery.Operator.OR)
        .queryList(List.of(boolQuery)).outcomeTrue(outcome1).build();
    List<BoolMultiQuery> clauses = List.of(boolMultiQuery1);

    BoolMultiNode node =
        BoolMultiNode.builder()
            .id("bool_node")
            .fields(List.of(field1))
            .clauses(clauses)
            .outcomeDefault(outcomeDefault)
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field1)).thenReturn(null);
    assertEquals(outcomeMissing, boolMultiNodeEvaluator.evaluate(node, variant));
  }

}