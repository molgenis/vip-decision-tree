package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.DecisionType.BOOL;
import static org.molgenis.vcf.decisiontree.filter.model.DecisionType.CATEGORICAL;
import static org.molgenis.vcf.decisiontree.filter.model.DecisionType.EXISTS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.*;

@ExtendWith(MockitoExtension.class)
class NodeEvaluatorServiceImplTest {
  @Mock
  private BoolNodeEvaluator boolNodeEvaluator;
  @Mock
  private BoolMultiNodeEvaluator boolMultiNodeEvaluator;
  @Mock
  private CategoricalNodeEvaluator categoricalNodeEvaluator;
  @Mock
  private ExistsNodeEvaluator existsNodeEvaluator;
  @Mock
  private ExpressionNodeEvaluator expressionNodeEvaluator;
  private NodeEvaluatorServiceImpl nodeEvaluatorService;

  @BeforeEach
  void setUp() {
    nodeEvaluatorService =
        new NodeEvaluatorServiceImpl(boolNodeEvaluator, boolMultiNodeEvaluator,
            categoricalNodeEvaluator,
            existsNodeEvaluator, expressionNodeEvaluator);
  }

  @Test
  void evaluateBoolNode() {
    BoolNode boolNode = when(mock(BoolNode.class).getDecisionType()).thenReturn(BOOL).getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(boolNodeEvaluator.evaluate(boolNode, variant, null)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(boolNode, variant));
  }

  @Test
  void evaluateExistsNode() {
    ExistsNode existsNode = when(mock(ExistsNode.class).getDecisionType()).thenReturn(EXISTS)
        .getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(existsNodeEvaluator.evaluate(existsNode, variant, null)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(existsNode, variant));
  }

  @Test
  void evaluateCategoricalNode() {
    CategoricalNode categoricalNode =
        when(mock(CategoricalNode.class).getDecisionType()).thenReturn(CATEGORICAL).getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(categoricalNodeEvaluator.evaluate(categoricalNode, variant, null)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(categoricalNode, variant));
  }

  @Test
  void evaluateCategoricalNodeMissing() {
    CategoricalNode categoricalNode =
        when(mock(CategoricalNode.class).getDecisionType()).thenReturn(CATEGORICAL).getMock();
    Variant variant = mock(Variant.class);
    NodeOutcome nodeOutcome = mock(NodeOutcome.class);
    when(categoricalNodeEvaluator.evaluate(categoricalNode, variant, null)).thenReturn(nodeOutcome);
    assertEquals(nodeOutcome, nodeEvaluatorService.evaluate(categoricalNode, variant));
  }
}
