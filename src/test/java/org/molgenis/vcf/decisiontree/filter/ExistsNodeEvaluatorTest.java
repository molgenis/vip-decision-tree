package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.ExistsNode;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;

class ExistsNodeEvaluatorTest {

  private ExistsNodeEvaluator existsNodeEvaluator;

  @BeforeEach
  void setUp() {
    existsNodeEvaluator = new ExistsNodeEvaluator();
  }

  @Test
  void evaluateExistsTrue() {
    FieldImpl field = mock(FieldImpl.class);

    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    ExistsNode node =
        ExistsNode.builder()
            .id("exists_node")
            .field(field)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field)).thenReturn("test");
    assertEquals(outcomeTrue, existsNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateExistsFalse() {
    FieldImpl field = mock(FieldImpl.class);

    NodeOutcome outcomeTrue = mock(NodeOutcome.class);
    NodeOutcome outcomeFalse = mock(NodeOutcome.class);
    ExistsNode node =
        ExistsNode.builder()
            .id("exists_node")
            .field(field)
            .outcomeTrue(outcomeTrue)
            .outcomeFalse(outcomeFalse)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field)).thenReturn(null);
    assertEquals(outcomeFalse, existsNodeEvaluator.evaluate(node, variant));
  }
}