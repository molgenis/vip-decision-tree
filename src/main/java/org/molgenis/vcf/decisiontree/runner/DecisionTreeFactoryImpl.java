package org.molgenis.vcf.decisiontree.runner;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.loader.model.*;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
class DecisionTreeFactoryImpl implements DecisionTreeFactory {

  public static final String FILE_COMMENT_CHARACTER = "#";
  private final QueryValidator queryValidator;

  DecisionTreeFactoryImpl(QueryValidator queryValidator) {
    this.queryValidator = requireNonNull(queryValidator);
  }

  @Override
  public DecisionTree map(VcfMetadata vcfMetadata, Settings settings) {
    ConfigDecisionTree configDecisionTree = settings.getConfigDecisionTree();
    Map<String, Label> labelMap = mapLabels(configDecisionTree.getLabels());
    Map<String, Set<String>> filesMap = mapFiles(configDecisionTree.getFiles());
    Map<String, Node> nodeMap = mapNodes(vcfMetadata, configDecisionTree.getNodes(), labelMap,
        filesMap);
    Node rootNode = nodeMap.get(configDecisionTree.getRootNode());
    return DecisionTree.builder().rootNode(rootNode).build();
  }

  private Map<String, Set<String>> mapFiles(Map<String, Path> files) {
    Map<String, Set<String>> filesMap;
    if (files == null) {
      filesMap = emptyMap();
    } else {
      filesMap = new HashMap<>();
      files.entrySet()
          .forEach(entry -> filesMap.put(entry.getKey(), this.mapFile(entry.getValue())));
    }
    return filesMap;
  }

  private Set<String> mapFile(Path path) {
    try {
      return Files.readAllLines(path).stream()
          .filter(value -> (!value.startsWith(FILE_COMMENT_CHARACTER) && !value.isEmpty()))
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
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
      VcfMetadata vcfMetadata, Map<String, ConfigNode> configNodeMap, Map<String, Label> labelMap,
      Map<String, Set<String>> files) {
    // first pass: create nodes
    Map<String, Node> nodeMap =
        configNodeMap.entrySet().stream()
            .map(entry -> this.toNode(vcfMetadata, entry.getKey(), entry.getValue(), files))
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

  private Node toNode(VcfMetadata vcfMetadata, String id, ConfigNode configNode,
      Map<String, Set<String>> files) {
    Node node;
    switch (configNode.getType()) {
      case EXISTS:
        node = toExistsNode(vcfMetadata, id, (ConfigExistsNode) configNode);
        break;
      case BOOL:
        node = toBoolNode(vcfMetadata, id, (ConfigBoolNode) configNode, files);
        break;
      case BOOL_MULTI:
        node = toBoolMultiNode(vcfMetadata, id, (ConfigBoolMultiNode) configNode, files);
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

  private Node toExistsNode(VcfMetadata vcfMetadata, String id, ConfigExistsNode configNode) {
    Field field = vcfMetadata.getField(configNode.getField());
    return ExistsNode.builder()
        .id(id)
        .field(field)
        .description(configNode.getDescription())
        .build();
  }

  private BoolNode toBoolNode(VcfMetadata vcfMetadata, String id, ConfigBoolNode nodeConfig,
      Map<String, Set<String>> files) {
    BoolQuery boolQuery = toBoolQuery(vcfMetadata, nodeConfig.getQuery(), files);
    return BoolNode.builder()
        .id(id)
        .description(nodeConfig.getDescription())
        .query(boolQuery)
        .build();
  }

  private BoolQuery toBoolQuery(VcfMetadata vcfMetadata, ConfigBoolQuery configBoolQuery,
      Map<String, Set<String>> files) {
    Operator operator = toOperator(configBoolQuery.getOperator());
    MultiMode multiMode = toMultiMode(configBoolQuery.getMultiMode());
    Field field = vcfMetadata.getField(configBoolQuery.getField());
    queryValidator.validateBooleanNode(configBoolQuery, field);
    Object value = configBoolQuery.getValue();
    if (value.toString().startsWith(FILE_PREFIX)) {
      value = files.get(((String) value).substring(FILE_PREFIX.length()));
    }
    return BoolQuery.builder()
        .multiMode(multiMode)
        .field(field)
        .operator(operator)
        .value(value)
        .build();
  }

  private BoolMultiNode toBoolMultiNode(VcfMetadata vcfMetadata, String id,
      ConfigBoolMultiNode nodeConfig,
      Map<String, Set<String>> files) {
    List<BoolMultiQuery> boolMultiQueries = nodeConfig.getOutcomes().stream()
        .map(clause -> toBoolClause(vcfMetadata, clause, files)).toList();
    List<Field> fields = nodeConfig.getFields().stream().map(vcfMetadata::getField)
        .toList();
    return BoolMultiNode.builder()
        .id(id)
        .fields(fields)
        .description(nodeConfig.getDescription())
        .clauses(boolMultiQueries)
        .build();
  }

  private BoolMultiQuery toBoolClause(VcfMetadata vcfMetadata,
      ConfigBoolMultiQuery configBoolMultiQuery,
      Map<String, Set<String>> files) {
    List<BoolQuery> queries = configBoolMultiQuery.getQueries().stream()
        .map(query -> toBoolQuery(vcfMetadata, query, files)).toList();
    return BoolMultiQuery.builder().id(configBoolMultiQuery.getId()).queryList(queries)
        .operator(toMultiQueryOperator(configBoolMultiQuery.getOperator()))
        .build();
  }

  private BoolMultiQuery.Operator toMultiQueryOperator(ConfigClauseOperator configOperator) {
    BoolMultiQuery.Operator operator;
    if (configOperator == null) {
      return null;
    }
    switch (configOperator) {
      case AND:
        operator = BoolMultiQuery.Operator.AND;
        break;
      case OR:
        operator = BoolMultiQuery.Operator.OR;
        break;
      default:
        throw new UnexpectedEnumException(configOperator);
    }
    return operator;
  }

  private MultiMode toMultiMode(ConfigMultiMode configMultiMode) {
    MultiMode multiMode;
    if (configMultiMode == null) {
      return null;
    }
    switch (configMultiMode) {
      case ALL:
        multiMode = MultiMode.ALL;
        break;
      case ANY:
        multiMode = MultiMode.ANY;
        break;
      case SINGLE:
        multiMode = MultiMode.SINGLE;
        break;
      default:
        throw new UnexpectedEnumException(configMultiMode);
    }
    return multiMode;
  }

  private Operator toOperator(ConfigOperator configOperator) {
    Operator operator;
    switch (configOperator) {
      case EQUALS:
        operator = Operator.EQUALS;
        break;
      case EQUALS_SEQUENCE:
        operator = Operator.EQUALS_SEQUENCE;
        break;
      case NOT_EQUALS_SEQUENCE:
        operator = Operator.NOT_EQUALS_SEQUENCE;
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
      case CONTAINS:
        operator = Operator.CONTAINS;
        break;
      case NOT_CONTAINS:
        operator = Operator.NOT_CONTAINS;
        break;
      case CONTAINS_ALL:
        operator = Operator.CONTAINS_ALL;
        break;
      case CONTAINS_ANY:
        operator = Operator.CONTAINS_ANY;
        break;
      case CONTAINS_NONE:
        operator = Operator.CONTAINS_NONE;
        break;
      case RANGE_OVERLAPS:
        operator = Operator.RANGE_OVERLAPS;
        break;
      case RANGE_BELOW:
        operator = Operator.RANGE_BELOW;
        break;
      case RANGE_ABOVE:
        operator = Operator.RANGE_ABOVE;
        break;
      default:
        throw new UnexpectedEnumException(configOperator);
    }
    return operator;
  }

  private CategoricalNode toCategoricalNode(
      VcfMetadata vcfMetadata, String id, ConfigCategoricalNode nodeConfig) {
    Field field = vcfMetadata.getField(nodeConfig.getField());
    queryValidator.validateCategoricalNode(field);
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
      case EXISTS:
        updateExistsNode((ExistsNode) node, (ConfigExistsNode) configNode, nodeMap, labelMap);
        break;
      case BOOL:
        updateBoolNode((BoolNode) node, (ConfigBoolNode) configNode, nodeMap, labelMap);
        break;
      case BOOL_MULTI:
        updateBoolMultiNode((BoolMultiNode) node, (ConfigBoolMultiNode) configNode, nodeMap,
            labelMap);
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

  private void updateExistsNode(
      ExistsNode node,
      ConfigExistsNode configNode,
      Map<String, Node> nodeMap,
      Map<String, Label> labelMap) {
    NodeOutcome outcomeTrue = toNodeOutcome(configNode.getOutcomeTrue(), nodeMap, labelMap);
    node.setOutcomeTrue(outcomeTrue);

    NodeOutcome outcomeFalse = toNodeOutcome(configNode.getOutcomeFalse(), nodeMap, labelMap);
    node.setOutcomeFalse(outcomeFalse);
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

  private void updateBoolMultiNode(
      BoolMultiNode node,
      ConfigBoolMultiNode configNode,
      Map<String, Node> nodeMap,
      Map<String, Label> labelMap) {
    Map<String, ConfigBoolMultiQuery> clauses = configNode.getOutcomes().stream()
        .collect(Collectors.toMap(ConfigBoolMultiQuery::getId, clause -> clause));
    node.setClauses(node.getClauses().stream()
        .map(clause -> updateClause(clause, clauses.get(clause.getId()), nodeMap, labelMap))
        .toList());

    NodeOutcome outcomeMissing = toNodeOutcome(configNode.getOutcomeMissing(), nodeMap, labelMap);
    node.setOutcomeMissing(outcomeMissing);

    NodeOutcome outcomeDefault = toNodeOutcome(configNode.getOutcomeDefault(), nodeMap, labelMap);
    node.setOutcomeDefault(outcomeDefault);
  }

  private BoolMultiQuery updateClause(BoolMultiQuery clause,
      ConfigBoolMultiQuery configBoolMultiQuery,
      Map<String, Node> nodeMap, Map<String, Label> labelMap) {
    NodeOutcome outcomeDefault = toNodeOutcome(configBoolMultiQuery.getOutcomeTrue(), nodeMap,
        labelMap);
    clause.setOutcomeTrue(outcomeDefault);
    return clause;
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
