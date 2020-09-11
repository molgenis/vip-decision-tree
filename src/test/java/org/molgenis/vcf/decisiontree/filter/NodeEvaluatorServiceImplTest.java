package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.DecisionType.BOOL;
import static org.molgenis.vcf.decisiontree.filter.model.DecisionType.CATEGORICAL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;

@ExtendWith(MockitoExtension.class)
class NodeEvaluatorServiceImplTest {
  @Mock private BoolNodeEvaluator boolNodeEvaluator;
  @Mock private CategoricalNodeEvaluator categoricalNodeEvaluator;
  private NodeEvaluatorServiceImpl nodeEvaluatorService;

  @BeforeEach
  void setUp() {
    nodeEvaluatorService =
        new NodeEvaluatorServiceImpl(boolNodeEvaluator, categoricalNodeEvaluator);
  }

  @Test
  void evaluateBoolNode() {
    BoolNode boolNode = when(mock(BoolNode.class).getDecisionType()).thenReturn(BOOL).getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(boolNodeEvaluator.evaluate(boolNode, variant)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(boolNode, variant));
  }

  @Test
  void evaluateCategoricalNode() {
    CategoricalNode categoricalNode =
        when(mock(CategoricalNode.class).getDecisionType()).thenReturn(CATEGORICAL).getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(categoricalNodeEvaluator.evaluate(categoricalNode, variant)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(categoricalNode, variant));
  }
}
