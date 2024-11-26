package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.INFO;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.Settings;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.DecisionTree;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.Mode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigLeafNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.springframework.util.ResourceUtils;

@ExtendWith(MockitoExtension.class)
class DecisionTreeFactoryImplTest{

  @Mock
  QueryValidator queryValidator;
  @Mock
  VcfMetadata vcfMetadata;
  @Mock
  ConfigDecisionTree decisionTree;

  private DecisionTreeFactoryImpl decisionTreeFactory;

  @BeforeEach
  void setUp() {
    decisionTreeFactory = new DecisionTreeFactoryImpl(queryValidator);
  }

  @ParameterizedTest
  @MethodSource("provideOperatorsForMap")
  void map(Operator expectedOperator, ConfigOperator configOperator) throws FileNotFoundException {
    Map<String, Path> files = Collections
        .singletonMap("test", ResourceUtils.getFile("classpath:test.txt").toPath());
    ConfigBoolQuery query =
        ConfigBoolQuery.builder().field("INFO/testField").operator(configOperator)
            .value(FILE_PREFIX + "test")
            .build();
    ConfigLeafNode leafNode = ConfigLeafNode.builder().label("label").clazz("end").build();
    ConfigNodeOutcome outcome = ConfigNodeOutcome.builder().nextNode("end").build();
    ConfigNode configNode = ConfigBoolNode.builder().label("label").query(query).outcomeTrue(
        outcome).outcomeFalse(outcome).build();
    Map<String, ConfigNode> nodes = Map.of("test", configNode, "end", leafNode);
    when(decisionTree.getLabels()).thenReturn(Collections.emptyMap());
    when(decisionTree.getFiles()).thenReturn(files);
    when(decisionTree.getNodes()).thenReturn(nodes);
    when(decisionTree.getRootNode()).thenReturn("test");
    when(vcfMetadata.getField("INFO/testField")).thenReturn(
        FieldImpl.builder().id("testField").fieldType(INFO).valueType(
            ValueType.STRING).valueCount(ValueCount.builder().type(ValueCount.Type.A).build()).build());
    Settings settings = Settings.builder().mode(Mode.VARIANT).configDecisionTree(decisionTree)
        .build();
    DecisionTree decisionTree = decisionTreeFactory.map(vcfMetadata, settings);

    assertAll(
        () -> assertEquals(expectedOperator,
            ((BoolNode) decisionTree.getRootNode()).getQuery().getOperator()),
        () -> assertEquals(Set.of("unit", "test", "value"),
            ((BoolNode) decisionTree.getRootNode()).getQuery().getValue())
    );
  }

  private static Stream<Arguments> provideOperatorsForMap() {
    return Stream.of(
        Arguments.of(Operator.IN, ConfigOperator.IN),
        Arguments.of(Operator.NOT_IN, ConfigOperator.NOT_IN),
        Arguments.of(Operator.CONTAINS, ConfigOperator.CONTAINS),
        Arguments.of(Operator.NOT_CONTAINS, ConfigOperator.NOT_CONTAINS),
        Arguments.of(Operator.CONTAINS_ALL, ConfigOperator.CONTAINS_ALL),
        Arguments.of(Operator.CONTAINS_ANY, ConfigOperator.CONTAINS_ANY),
        Arguments.of(Operator.CONTAINS_NONE, ConfigOperator.CONTAINS_NONE)
    );
  }
}