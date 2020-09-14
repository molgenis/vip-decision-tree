package org.molgenis.vcf.decisiontree.runner;

import static java.util.Collections.emptyMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.HashMap;
import java.util.Map;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.LeafNode;
import org.molgenis.vcf.decisiontree.filter.model.Node;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.filter.model.NodeType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLabel;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLeafNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
class DecisionTreeFactoryImpl implements DecisionTreeFactory {

  @Override
  public DecisionTree map(VcfMetadata vcfMetadata, Settings settings) {
    ConfigDecisionTree configDecisionTree = settings.getConfigDecisionTree();
    Map<String, Label> labelMap = mapLabels(configDecisionTree.getLabels());
    Map<String, Node> nodeMap = mapNodes(vcfMetadata, configDecisionTree.getNodes(), labelMap);
    Node rootNode = nodeMap.get(configDecisionTree.getRootNode());
    return DecisionTree.builder().rootNode(rootNode).build();
  }

  private Map<String, Label> mapLabels(@Nullable Map<String, ConfigLabel> configLabels) {
    Map<String, Label> labelMap;
    if (configLabels == null) {
      labelMap = emptyMap();
    } else {
      labelMap =
          configLabels.entrySet().stream()
              .map(entry -> this.mapLabel(entry.getKey(), entry.getValue()))
              .collect(toMap(Label::getId, identity()));
    }
    return labelMap;
  }

  private Label mapLabel(String id, ConfigLabel configLabel) {
    return Label.builder().id(id).description(configLabel.getDescription()).build();
  }

  private Map<String, Node> mapNodes(
      VcfMetadata vcfMetadata, Map<String, ConfigNode> configNodeMap, Map<String, Label> labelMap) {
    // first pass: create nodes
    Map<String, Node> nodeMap =
        configNodeMap.entrySet().stream()
            .map(entry -> this.toNode(vcfMetadata, entry.getKey(), entry.getValue()))
            .collect(toMap(Node::getId, identity()));

    // second pass: update nodes
    nodeMap
        .values()
        .forEach(
            node -> {
              ConfigNode configNode = configNodeMap.get(node.getId());
              updateNode(node, configNode, nodeMap, labelMap);
            });
    return nodeMap;
  }

  private Node toNode(VcfMetadata vcfMetadata, String id, ConfigNode configNode) {
    Node node;
    switch (configNode.getType()) {
      case BOOL:
        node = toBoolNode(vcfMetadata, id, (ConfigBoolNode) configNode);
        break;
      case CATEGORICAL:
        node = toCategoricalNode(vcfMetadata, id, (ConfigCategoricalNode) configNode);
        break;
      case LEAF:
        node = toLeafNode(id, (ConfigLeafNode) configNode);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unexpected enum '%s'", configNode.getType().toString()));
    }
    return node;
  }

  private BoolNode toBoolNode(VcfMetadata vcfMetadata, String id, ConfigBoolNode nodeConfig) {
    BoolQuery boolQuery = toBoolQuery(vcfMetadata, nodeConfig.getQuery());
    return BoolNode.builder()
        .id(id)
        .description(nodeConfig.getDescription())
        .query(boolQuery)
        .build();
  }

  // TODO validate query: is field/operator/value allowed?
  private BoolQuery toBoolQuery(VcfMetadata vcfMetadata, ConfigBoolQuery configBoolQuery) {
    Operator operator = toOperator(configBoolQuery.getOperator());
    Field field = vcfMetadata.getField(configBoolQuery.getField());
    return BoolQuery.builder()
        .field(field)
        .operator(operator)
        .value(configBoolQuery.getValue())
        .build();
  }

  private Operator toOperator(ConfigOperator configOperator) {
    Operator operator;
    switch (configOperator) {
      case EQUALS:
        operator = Operator.EQUALS;
        break;
      case NOT_EQUALS:
        operator = Operator.NOT_EQUALS;
        break;
      case LESS:
        operator = Operator.LESS;
        break;
      case LESS_OR_EQUAL:
        operator = Operator.LESS_OR_EQUAL;
        break;
      case GREATER:
        operator = Operator.GREATER;
        break;
      case GREATER_OR_EQUAL:
        operator = Operator.GREATER_OR_EQUAL;
        break;
      case IN:
        operator = Operator.IN;
        break;
      case NOT_IN:
        operator = Operator.NOT_IN;
        break;
      default:
        throw new UnexpectedEnumException(configOperator);
    }
    return operator;
  }

  // TODO validate whether node can work with this kind of field
  private CategoricalNode toCategoricalNode(
      VcfMetadata vcfMetadata, String id, ConfigCategoricalNode nodeConfig) {
    Field field = vcfMetadata.getField(nodeConfig.getField());
    return CategoricalNode.builder()
        .id(id)
        .description(nodeConfig.getDescription())
        .field(field)
        .build();
  }

  private LeafNode toLeafNode(String id, ConfigLeafNode nodeConfig) {
    return LeafNode.builder()
        .id(id)
        .description(nodeConfig.getDescription())
        .clazz(nodeConfig.getClazz())
        .build();
  }

  private void updateNode(
      Node node, ConfigNode configNode, Map<String, Node> nodeMap, Map<String, Label> labelMap) {
    if (node.getNodeType() == NodeType.LEAF) {
      return;
    }

    switch (configNode.getType()) {
      case BOOL:
        updateBoolNode((BoolNode) node, (ConfigBoolNode) configNode, nodeMap, labelMap);
        break;
      case CATEGORICAL:
        updateEnumNode(
            (CategoricalNode) node, (ConfigCategoricalNode) configNode, nodeMap, labelMap);
        break;
      default:
        throw new IllegalArgumentException(
            String.format("unexpected enum '%s'", configNode.getType().toString()));
    }
  }

  private void updateBoolNode(
      BoolNode node,
      ConfigBoolNode configNode,
      Map<String, Node> nodeMap,
      Map<String, Label> labelMap) {
    NodeOutcome outcomeTrue = toNodeOutcome(configNode.getOutcomeTrue(), nodeMap, labelMap);
    node.setOutcomeTrue(outcomeTrue);

    NodeOutcome outcomeFalse = toNodeOutcome(configNode.getOutcomeFalse(), nodeMap, labelMap);
    node.setOutcomeFalse(outcomeFalse);

    NodeOutcome outcomeMissing = toNodeOutcome(configNode.getOutcomeMissing(), nodeMap, labelMap);
    node.setOutcomeMissing(outcomeMissing);
  }

  private void updateEnumNode(
      CategoricalNode node,
      ConfigCategoricalNode configNode,
      Map<String, Node> nodeMap,
      Map<String, Label> labelMap) {
    Map<String, NodeOutcome> outcomeMap = new HashMap<>();
    configNode
        .getOutcomeMap()
        .forEach(
            (key, configOutcome) -> {
              NodeOutcome outcome = toNodeOutcome(configOutcome, nodeMap, labelMap);
              outcomeMap.put(key, outcome);
            });
    node.setOutcomeMap(outcomeMap);

    NodeOutcome outcomeMissing = toNodeOutcome(configNode.getOutcomeMissing(), nodeMap, labelMap);
    node.setOutcomeMissing(outcomeMissing);

    NodeOutcome outcomeDefault = toNodeOutcome(configNode.getOutcomeDefault(), nodeMap, labelMap);
    node.setOutcomeDefault(outcomeDefault);
  }

  private NodeOutcome toNodeOutcome(
      @Nullable ConfigNodeOutcome configNodeOutcome,
      Map<String, Node> nodeMap,
      Map<String, Label> labelMap) {
    NodeOutcome nodeOutcome;
    if (configNodeOutcome != null) {
      Node nextNode = nodeMap.get(configNodeOutcome.getNextNode());
      Label label = labelMap.get(configNodeOutcome.getLabel());
      nodeOutcome = NodeOutcome.builder().nextNode(nextNode).label(label).build();
    } else {
      nodeOutcome = null;
    }
    return nodeOutcome;
  }
}
