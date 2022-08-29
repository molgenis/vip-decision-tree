package org.molgenis.vcf.decisiontree.loader;

import static java.lang.String.format;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigExistsNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLeafNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
class ConfigDecisionTreeValidatorImpl implements ConfigDecisionTreeValidator {

  private static final Pattern PATTERN_ALPHANUMERIC_UNDERSCORE = Pattern.compile("[a-zA-Z0-9_]+");
  public static final String OUTCOME_TRUE = "outcomeTrue";
  public static final String OUTCOME_FALSE = "outcomeFalse";
  public static final String OUTCOME_MISSING = "outcomeMissing";
  public static final String OUTCOME_DEFAULT = "outcomeDefault";

  @Override
  public void validate(ConfigDecisionTree configDecisionTree) {
    validateRootNode(configDecisionTree);
    validateNodes(configDecisionTree);
  }

  private void validateRootNode(ConfigDecisionTree configDecisionTree) {
    String rootNodeId = configDecisionTree.getRootNode();
    if (!configDecisionTree.getNodes().containsKey(rootNodeId)) {
      throw new ConfigDecisionTreeValidationException(
          format("'rootNode' value '%s' refers to unknown node", rootNodeId));
    }
  }

  private void validateNodes(ConfigDecisionTree configDecisionTree) {
    Map<String, ConfigNode> nodes = configDecisionTree.getNodes();
    Map<String, Path> files = configDecisionTree.getFiles();
    nodes.forEach((key, value) -> validateNode(key, value, nodes, files));
  }

  private void validateNode(String id, ConfigNode node, Map<String, ConfigNode> nodes,
      Map<String, Path> files) {
    validateAlphanumericValue("id", id);
    switch (node.getType()) {
      case EXISTS:
        validateExistsNode(id, (ConfigExistsNode) node, nodes);
        break;
      case BOOL:
        validateBoolNode(id, (ConfigBoolNode) node, nodes, files);
        break;
      case BOOL_MULTI:
        validateBoolMultiNode(id, (ConfigBoolMultiNode) node, nodes, files);
        break;
      case CATEGORICAL:
        validateCategoricalNode(id, (ConfigCategoricalNode) node, nodes);
        break;
      case LEAF:
        validateLeafNode(id, (ConfigLeafNode) node);
        break;
      default:
        throw new UnexpectedEnumException(node.getType());
    }
  }

  private void validateExistsNode(String id, ConfigExistsNode node, Map<String, ConfigNode> nodes) {
    validateOutcome(id, OUTCOME_TRUE, nodes, node.getOutcomeTrue());
    validateOutcome(id, OUTCOME_FALSE, nodes, node.getOutcomeFalse());
  }

  private void validateBoolNode(String id, ConfigBoolNode node, Map<String, ConfigNode> nodes,
      Map<String, Path> files) {
    validateValue(id, node.getQuery(), files);
    validateOutcome(id, OUTCOME_TRUE, nodes, node.getOutcomeTrue());
    validateOutcome(id, OUTCOME_FALSE, nodes, node.getOutcomeFalse());
    validateOutcome(id, OUTCOME_MISSING, nodes, node.getOutcomeMissing());
  }


  private void validateBoolMultiNode(String id, ConfigBoolMultiNode node,
      Map<String, ConfigNode> nodes, Map<String, Path> files) {
    validateFields(node);
    validateOutcomes(id, node.getOutcomes(), nodes, files);
    validateOutcome(id, OUTCOME_MISSING, nodes, node.getOutcomeMissing());
    validateOutcome(id, OUTCOME_DEFAULT, nodes, node.getOutcomeDefault());
  }

  private void validateFields(ConfigBoolMultiNode node) {
    List<String> fields = node.getFields();
    for (ConfigBoolMultiQuery clause : node.getOutcomes()) {
      for (ConfigBoolQuery query : clause.getQueries()) {
        if (!fields.contains(query.getField())) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  "Field '%s' refers to a field that is not present in the 'fields' list of node '%s'",
                  query.getField(), node.getId()));
        }
      }
    }
  }

  private void validateOutcomes(String id, List<ConfigBoolMultiQuery> outcomes,
      Map<String, ConfigNode> nodes,
      Map<String, Path> files) {
    for (ConfigBoolMultiQuery outcome : outcomes) {
      if (outcome.getQueries().size() == 1) {
        if (outcome.getOperator() != null) {
          throw new ConfigDecisionTreeValidationException(String.format(
              "MultiBool node '%s' contains an outcome with a single query but with an operator.",
              id));
        }
      } else if (outcome.getQueries().size() > 1) {
        if (outcome.getOperator() == null) {
          throw new ConfigDecisionTreeValidationException(String.format(
              "MultiBool node '%s' contains an outcome with multiple queries but without an operator.",
              id));
        }
      } else {
        throw new ConfigDecisionTreeValidationException(
            String.format("MultiBool node '%s' contains an outcome without any queries.", id));
      }

      for (ConfigBoolQuery query : outcome.getQueries()) {
        validateValue(id, query, files);
      }
      validateOutcome(id, OUTCOME_TRUE, nodes, outcome.getOutcomeTrue());
    }
  }

  private void validateValue(String id, ConfigBoolQuery query, Map<String, Path> files) {
    Object value = query.getValue();
    if (value instanceof String && value.toString().startsWith(FILE_PREFIX)) {
      String file = value.toString().substring(FILE_PREFIX.length());
      if (!files.containsKey(file)) {
        throw new ConfigDecisionTreeValidationException(
            format("Unknown file value '%s' for node %s", file, id));
      }
    }
  }

  private void validateCategoricalNode(
      String id, ConfigCategoricalNode node, Map<String, ConfigNode> nodes) {
    node.getOutcomeMap()
        .forEach(
            (key, outcome) -> {
              if (outcome != null) {
                validateOutcome(id, "outcomeMap." + key, nodes, outcome);
              } else {
                throw new ConfigDecisionTreeValidationException(
                    format("node '%s.%s.%s' can't be null.", id, "outcomeMap", key));
              }
            });

    validateOutcome(id, OUTCOME_DEFAULT, nodes, node.getOutcomeDefault());
    validateOutcome(id, OUTCOME_MISSING, nodes, node.getOutcomeMissing());
  }

  private void validateLeafNode(String id, ConfigLeafNode node) {
    validateAlphanumericValue(id + ".class", node.getClazz());
  }

  private void validateOutcome(
      String id,
      String field,
      Map<String, ConfigNode> nodes,
      @Nullable ConfigNodeOutcome nodeOutcome) {
    if (nodeOutcome == null) {
      return;
    }

    if (!nodes.containsKey(nodeOutcome.getNextNode())) {
      throw new ConfigDecisionTreeValidationException(
          format("node '%s.%s' refers to unknown node '%s'.", id, field, nodeOutcome));
    }
    validateAlphanumericValue(id + '.' + field, nodeOutcome.getLabel());
  }

  private void validateAlphanumericValue(String field, @Nullable String value) {
    if (value == null) {
      return;
    }

    if (!PATTERN_ALPHANUMERIC_UNDERSCORE.matcher(value).matches()) {
      throw new ConfigDecisionTreeValidationException(
          format(
              "node '%s' with value '%s' contains illegal characters (valid values are a-zA-Z0-9_).",
              field, value));
    }
  }
}
