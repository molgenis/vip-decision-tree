package org.molgenis.vcf.decisiontree.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.LeafNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;

@ExtendWith(MockitoExtension.class)
class DecisionTreeExecutorImplTest {
  @Mock private NodeEvaluatorService nodeEvaluatorService;
  private DecisionTreeExecutorImpl decisionTreeExecutor;

  @Test
  void execute() {
    decisionTreeExecutor = new DecisionTreeExecutorImpl(nodeEvaluatorService);

    String clazz = "pass";

    CategoricalNode catNode = mock(CategoricalNode.class);
    BoolNode boolNode = mock(BoolNode.class);
    LeafNode leafNode = LeafNode.builder().id("my_id").label("my_label").clazz(clazz).build();
    DecisionTree decisionTree = DecisionTree.builder().rootNode(catNode).build();

    Variant variant = mock(Variant.class);

    NodeOutcome catNodeOutcome = mock(NodeOutcome.class);
    when(catNodeOutcome.getNextNode()).thenReturn(boolNode);
    when(nodeEvaluatorService.evaluate(catNode, variant, null)).thenReturn(catNodeOutcome);

    NodeOutcome boolNodeOutcome = mock(NodeOutcome.class);
    when(boolNodeOutcome.getNextNode()).thenReturn(leafNode);
    when(nodeEvaluatorService.evaluate(boolNode, variant, null)).thenReturn(boolNodeOutcome);

    Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
    assertEquals(
        Decision.builder().clazz(clazz).path(List.of()).labels(Set.of()).build(), decision);
  }

  @Test
  void executeStorePathAndLabels() {
    decisionTreeExecutor = new DecisionTreeExecutorImpl(nodeEvaluatorService, true, true);

    String clazz = "pass";

    CategoricalNode catNode = mock(CategoricalNode.class);
    BoolNode boolNode = mock(BoolNode.class);
    LeafNode leafNode = LeafNode.builder().id("my_id").label("my_label").clazz(clazz).build();
    DecisionTree decisionTree = DecisionTree.builder().rootNode(catNode).build();

    Variant variant = mock(Variant.class);

    NodeOutcome catNodeOutcome = mock(NodeOutcome.class);
    when(catNodeOutcome.getNextNode()).thenReturn(boolNode);
    when(nodeEvaluatorService.evaluate(catNode, variant, null)).thenReturn(catNodeOutcome);

    Label label = mock(Label.class);
    NodeOutcome boolNodeOutcome = mock(NodeOutcome.class);
    when(boolNodeOutcome.getLabel()).thenReturn(label);
    when(boolNodeOutcome.getNextNode()).thenReturn(leafNode);
    when(nodeEvaluatorService.evaluate(boolNode, variant, null)).thenReturn(boolNodeOutcome);

    Decision decision = decisionTreeExecutor.execute(decisionTree, variant);
    assertEquals(
        Decision.builder()
            .path(List.of(catNode, boolNode, leafNode))
            .labels(Set.of(label))
            .clazz(clazz)
            .build(),
        decision);
  }
}
