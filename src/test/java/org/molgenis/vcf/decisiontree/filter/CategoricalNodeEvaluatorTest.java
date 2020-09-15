package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;

class CategoricalNodeEvaluatorTest {

  private CategoricalNodeEvaluator categoricalNodeEvaluator;

  @BeforeEach
  void setUp() {
    categoricalNodeEvaluator = new CategoricalNodeEvaluator();
  }

  @Test
  void evaluate() {
    Field field = mock(Field.class);
    String key = "key";
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    Map<String, NodeOutcome> outcomeMap = Map.of(key, nodeOutcome);
    CategoricalNode node =
        CategoricalNode.builder().id("cat_id").field(field).outcomeMap(outcomeMap).build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field)).thenReturn(key);
    assertEquals(nodeOutcome, categoricalNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateDefault() {
    Field field = mock(Field.class);
    String key = "key";
    NodeOutcome outcomeDefault = mock(NodeOutcome.class);
    CategoricalNode node =
        CategoricalNode.builder()
            .id("cat_id")
            .field(field)
            .outcomeMap(Map.of())
            .outcomeDefault(outcomeDefault)
            .build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field)).thenReturn(key);
    assertEquals(outcomeDefault, categoricalNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateMissing() {
    Field field = mock(Field.class);
    NodeOutcome outcomeMissing = mock(NodeOutcome.class);
    CategoricalNode node =
        CategoricalNode.builder()
            .id("cat_id")
            .field(field)
            .outcomeMap(Map.of())
            .outcomeMissing(outcomeMissing)
            .build();

    Variant variant = mock(Variant.class);
    assertEquals(outcomeMissing, categoricalNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateDefaultMissing() {
    Field field = mock(Field.class);
    String key = "key";
    CategoricalNode node =
        CategoricalNode.builder().id("cat_id").field(field).outcomeMap(Map.of()).build();

    Variant variant = mock(Variant.class);
    when(variant.getValue(field)).thenReturn(key);
    assertThrows(EvaluationException.class, () -> categoricalNodeEvaluator.evaluate(node, variant));
  }

  @Test
  void evaluateMissingMissing() {
    Field field = mock(Field.class);
    CategoricalNode node =
        CategoricalNode.builder().id("cat_id").field(field).outcomeMap(Map.of()).build();

    Variant variant = mock(Variant.class);
    assertThrows(EvaluationException.class, () -> categoricalNodeEvaluator.evaluate(node, variant));
  }
}
