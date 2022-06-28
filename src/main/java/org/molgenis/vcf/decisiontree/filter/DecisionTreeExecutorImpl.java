package org.molgenis.vcf.decisiontree.filter;

import static java.util.Objects.requireNonNull;
import static org.molgenis.vcf.decisiontree.filter.model.NodeType.LEAF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.DecisionNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.LeafNode;
import org.molgenis.vcf.decisiontree.filter.model.Node;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.SampleMeta;

public class DecisionTreeExecutorImpl implements DecisionTreeExecutor {

  private final NodeEvaluatorService nodeEvaluatorService;
  private final boolean storeLabels;
  private final boolean storePaths;

  /**
   * Constructs a DecisionTreeExecutor that doesn't store labels and paths.
   */
  public DecisionTreeExecutorImpl(NodeEvaluatorService nodeEvaluatorService) {
    this(nodeEvaluatorService, false, false);
  }

  public DecisionTreeExecutorImpl(
      NodeEvaluatorService nodeEvaluatorService, boolean storeLabels, boolean storePaths) {
    this.nodeEvaluatorService = requireNonNull(nodeEvaluatorService);
    this.storeLabels = storeLabels;
    this.storePaths = storePaths;
  }

  @Override
  public Decision execute(DecisionTree tree, Variant variant, SampleMeta sampleMeta) {
    List<Node> nodePath = storePaths ? new ArrayList<>() : List.of();
    Set<Label> labels = storeLabels ? new HashSet<>() : Set.of();

    Node currentNode = tree.getRootNode();
    do {
      if (storePaths) {
        nodePath.add(currentNode);
      }

      if (currentNode.getNodeType() == LEAF) {
        break;
      }

      NodeOutcome nodeOutcome;
      if (sampleMeta != null) {
        nodeOutcome = nodeEvaluatorService.evaluate((DecisionNode) currentNode, variant,
            sampleMeta);
      } else {
        nodeOutcome = nodeEvaluatorService.evaluate((DecisionNode) currentNode, variant);
      }
      if (storeLabels) {
        storeLabel(nodeOutcome, labels);
      }

      currentNode = nodeOutcome.getNextNode();
    } while (true);

    return new Decision(((LeafNode) currentNode).getClazz(), nodePath, labels);
  }

  private void storeLabel(NodeOutcome nodeOutcome, Set<Label> labels) {
    Label label = nodeOutcome.getLabel();
    if (label != null) {
      labels.add(label);
    }
  }
}
