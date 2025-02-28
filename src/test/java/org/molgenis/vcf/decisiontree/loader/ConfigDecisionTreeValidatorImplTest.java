package org.molgenis.vcf.decisiontree.loader;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigClauseOperator.AND;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigClauseOperator.OR;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.CONTAINS_ANY;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.CONTAINS_NONE;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.EQUALS;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.IN;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
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
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("INFO/field").operator(EQUALS)
        .value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateUnknownSampleField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("SAMPLE/TEST")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateValidSampleField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("SAMPLE/FATHER_ID")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }


  @Test
  void validateInvalidGenotypeField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("FORMAT/GENOTYPE/TEST")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateValidGenotypeField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("FORMAT/GENOTYPE/PHASED")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateValidListValueField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("INFO/test")
        .operator(CONTAINS_ANY).value(List.of(1, 2, 3)).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateValidListInvalueField() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("INFO/test")
        .operator(CONTAINS_NONE).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolNodeFileValue() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("INFO/field").operator(IN)
        .value(FILE_PREFIX + "testFile").build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .files(Map.of("testFile", Path.of("testFile")))
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolMultiNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery1 = ConfigBoolQuery.builder().field("INFO/field1")
        .operator(EQUALS).value(1).build();
    ConfigBoolQuery configBoolQuery2 = ConfigBoolQuery.builder().field("INFO/field2")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolMultiQuery clause1 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    ConfigBoolMultiQuery clause2 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, OR);
    List<ConfigBoolMultiQuery> clauses = List.of(clause1, clause2);
    ConfigBoolMultiNode configBoolMultiNode = ConfigBoolMultiNode.builder().id("node1")
        .fields(List.of("INFO/field1", "INFO/field2")).outcomes(clauses)
        .outcomeDefault(configNodeOutcome).outcomeMissing(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolMultiNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolMultiNodeSingleOr() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery1 = ConfigBoolQuery.builder().field("INFO/field1")
        .operator(EQUALS).value(1).build();
    ConfigBoolQuery configBoolQuery2 = ConfigBoolQuery.builder().field("INFO/field2")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolMultiQuery clause1 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    ConfigBoolMultiQuery clause2 = new ConfigBoolMultiQuery(null, List.of(configBoolQuery1),
        configNodeOutcome, OR);
    List<ConfigBoolMultiQuery> clauses = List.of(clause1, clause2);
    ConfigBoolMultiNode configBoolMultiNode = ConfigBoolMultiNode.builder().id("node1")
        .fields(List.of("INFO/field1", "INFO/field2")).outcomes(clauses)
        .outcomeDefault(configNodeOutcome).outcomeMissing(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolMultiNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateNodeLabelMissing() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("exit")
            .nodes(Map.of("exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
            () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolMultiNodeMissingOperator() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery1 = ConfigBoolQuery.builder().field("INFO/field1")
        .operator(EQUALS).value(1).build();
    ConfigBoolQuery configBoolQuery2 = ConfigBoolQuery.builder().field("INFO/field2")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolMultiQuery clause1 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    ConfigBoolMultiQuery clause2 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, null);
    List<ConfigBoolMultiQuery> clauses = List.of(clause1, clause2);
    ConfigBoolMultiNode configBoolMultiNode = ConfigBoolMultiNode.builder().id("node1")
        .fields(List.of("INFO/field1", "INFO/field2")).outcomes(clauses)
        .outcomeDefault(configNodeOutcome).outcomeMissing(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolMultiNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolMultiNodeFieldMismatch() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery1 = ConfigBoolQuery.builder().field("INFO/field1")
        .operator(EQUALS).value(1).build();
    ConfigBoolQuery configBoolQuery2 = ConfigBoolQuery.builder().field("INFO/field2")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolMultiQuery clause1 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    ConfigBoolMultiQuery clause2 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    List<ConfigBoolMultiQuery> clauses = List.of(clause1, clause2);
    ConfigBoolMultiNode configBoolMultiNode = ConfigBoolMultiNode.builder().id("node1")
        .fields(List.of("INFO/field1")).outcomes(clauses).outcomeDefault(configNodeOutcome)
        .outcomeMissing(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolMultiNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }


  @Test
  void validateBoolMultiNodeNoQueries() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery1 = ConfigBoolQuery.builder().field("INFO/field1")
        .operator(EQUALS).value(1).build();
    ConfigBoolQuery configBoolQuery2 = ConfigBoolQuery.builder().field("INFO/field2")
        .operator(EQUALS).value(1).build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolMultiQuery clause1 = new ConfigBoolMultiQuery(null,
        List.of(configBoolQuery1, configBoolQuery2), configNodeOutcome, AND);
    ConfigBoolMultiQuery clause2 = new ConfigBoolMultiQuery(null, emptyList(), configNodeOutcome,
        AND);
    List<ConfigBoolMultiQuery> clauses = List.of(clause1, clause2);
    ConfigBoolMultiNode configBoolMultiNode = ConfigBoolMultiNode.builder().id("node1")
        .fields(List.of("INFO/field1", "INFO/field2")).outcomes(clauses)
        .outcomeDefault(configNodeOutcome).outcomeMissing(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configBoolMultiNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateCategoricalNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigCategoricalNode categoricalNode = ConfigCategoricalNode.builder().field("INFO/field")
        .outcomeMap(Map.of("option", configNodeOutcome)).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", categoricalNode, "exit", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateLeafNode() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", configLeafNode)).build();
    assertDoesNotThrow(() -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateUnknownNode() {
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("unknownNode")
        .nodes(emptyMap()).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateIllegalNodeId() {
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("my&node")
        .nodes(Map.of("my&node", mock(ConfigNode.class))).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBooleanNodeOutcomeTrueNodeUnknown() {
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("INFO/field").operator(EQUALS)
        .value(1).build();
    ConfigNodeOutcome unknownNode = ConfigNodeOutcome.builder().nextNode("unknownNode").build();
    ConfigBoolNode boolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(unknownNode).outcomeFalse(unknownNode).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", boolNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateBoolNodeUnknownFileValue() {
    ConfigLeafNode configLeafNode = ConfigLeafNode.builder().clazz("class").label("label").build();
    ConfigBoolQuery configBoolQuery = ConfigBoolQuery.builder().field("field").operator(IN)
        .value(FILE_PREFIX + "testFile").build();
    ConfigNodeOutcome configNodeOutcome = ConfigNodeOutcome.builder().nextNode("exit").build();
    ConfigBoolNode configBoolNode = ConfigBoolNode.builder().query(configBoolQuery)
        .outcomeTrue(configNodeOutcome).outcomeFalse(configNodeOutcome).label("label").build();
    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .files(Map.of("otherTestFile", Path.of("otherTestFile")))
        .nodes(Map.of("node", configBoolNode, "exit", configLeafNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }


  @Test
  void validateCategoricalNodeOutcomeMapUnknown() {
    ConfigNodeOutcome unknownNode = ConfigNodeOutcome.builder().nextNode("unknownNode").build();
    ConfigCategoricalNode categoricalNode = ConfigCategoricalNode.builder().field("field").label("label")
        .outcomeMap(Map.of("option", unknownNode)).build();

    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", categoricalNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }

  @Test
  void validateCategoricalNodeOutcomeMapNull() {
    ConfigCategoricalNode categoricalNode = ConfigCategoricalNode.builder().field("field").label("label")
        .outcomeMap(singletonMap("option", null)).build();

    ConfigDecisionTree configDecisionTree = ConfigDecisionTree.builder().rootNode("node")
        .nodes(Map.of("node", categoricalNode)).build();
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> configDecisionTreeValidator.validate(configDecisionTree));
  }
}
