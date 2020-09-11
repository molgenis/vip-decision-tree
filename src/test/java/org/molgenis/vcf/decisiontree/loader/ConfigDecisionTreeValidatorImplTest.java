package org.molgenis.vcf.decisiontree.loader;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.EQUALS;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigCategoricalNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLeafNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;

class ConfigDecisionTreeValidatorImplTest {
  private ConfigDecisionTreeValidatorImpl configDecisionTreeValidator;

  @BeforeEach
  void setUp() {
    configDecisionTreeValidator = new ConfigDecisionTreeValidatorImpl();
  }

  @Test
  void validateBoolNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").build();
    ConfigBoolQuery configBoolQuery =
        ConfigBoolQuery.builder().field("field").operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode =
        ConfigBoolNode.builder()
            .query(configBoolQuery)
            .outcomeTrue(configNodeOutcome)
            .outcomeFalse(configNodeOutcome)
            .build();
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder()
            .rootNode("node")
            .nodes(Map.of("node", configBoolNode, "exit", configLeafNode))
            .build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateCategoricalNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigCategoricalNode categoricalNode =
        ConfigCategoricalNode.builder()
            .field("field")
            .outcomeMap(Map.of("option", configNodeOutcome))
            .build();
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder()
            .rootNode("node")
            .nodes(Map.of("node", categoricalNode, "exit", configLeafNode))
            .build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateLeafNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").build();
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder().rootNode("node").nodes(Map.of("node", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateUnknownNode() {
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder().rootNode("unknownNode").nodes(emptyMap()).build();
    assertThrows(
        ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateIllegalNodeId() {
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder()
            .rootNode("my&node")
            .nodes(Map.of("my&node", mock(ConfigNode.class)))
            .build();
    assertThrows(
        ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBooleanNodeOutcomeTrueNodeUnknown() {
    ConfigBoolQuery configBoolQuery =
        ConfigBoolQuery.builder().field("field").operator(EQUALS).value(1).build();
    ConfigNodeOutcome unknownNode = ConfigNodeOutcome.builder().nextNode("unknownNode").build();
    ConfigBoolNode boolNode =
        ConfigBoolNode.builder()
            .query(configBoolQuery)
            .outcomeTrue(unknownNode)
            .outcomeFalse(unknownNode)
            .build();
    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder().rootNode("node").nodes(Map.of("node", boolNode)).build();
    assertThrows(
        ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateCategoricalNodeOutcomeMapUnknown() {
    ConfigNodeOutcome unknownNode = ConfigNodeOutcome.builder().nextNode("unknownNode").build();
    ConfigCategoricalNode categoricalNode =
        ConfigCategoricalNode.builder()
            .field("field")
            .outcomeMap(Map.of("option", unknownNode))
            .build();

    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder()
            .rootNode("node")
            .nodes(Map.of("node", categoricalNode))
            .build();
    assertThrows(
        ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateCategoricalNodeOutcomeMapNull() {
    ConfigCategoricalNode categoricalNode =
        ConfigCategoricalNode.builder()
            .field("field")
            .outcomeMap(singletonMap("option", null))
            .build();

    ConfigDecisionTree configDecisionTree =
        ConfigDecisionTree.builder()
            .rootNode("node")
            .nodes(Map.of("node", categoricalNode))
            .build();
    assertThrows(
        ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }
}
