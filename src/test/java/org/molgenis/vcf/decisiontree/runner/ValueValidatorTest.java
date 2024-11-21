package org.molgenis.vcf.decisiontree.runner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;
import org.molgenis.vcf.decisiontree.filter.model.FieldType;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeValidationException;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNodeOutcome;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;

@ExtendWith(MockitoExtension.class)
class ValueValidatorTest {

  @Mock
  VcfMetadata vcfMetadata;
  @Mock
  ConfigDecisionTree configDecisionTree;
  ConfigNodeOutcome configNodeOutcome = new ConfigNodeOutcome("nextNode", "label");

  @ParameterizedTest
  @MethodSource("provideValidValues")
  void validate(Object value, Field field) {
    when(vcfMetadata.getField("field")).thenReturn(field);
    when(configDecisionTree.getNodes()).thenReturn(Map.of("node",
        new ConfigBoolNode("", "label", new ConfigBoolQuery("field", ConfigOperator.EQUALS, value),
            configNodeOutcome, configNodeOutcome, configNodeOutcome)));
    assertDoesNotThrow(() -> ValueValidator.validate(configDecisionTree, vcfMetadata));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidValues")
  void validateInvalid(Object value, Field field) {
    when(vcfMetadata.getField("field")).thenReturn(field);
    when(configDecisionTree.getNodes()).thenReturn(Map.of("node",
        new ConfigBoolNode("", "label", new ConfigBoolQuery("field", ConfigOperator.EQUALS, value),
            configNodeOutcome, configNodeOutcome, configNodeOutcome)));
    assertThrows(ConfigDecisionTreeValidationException.class,
        () -> ValueValidator.validate(configDecisionTree, vcfMetadata));
  }

  private static Stream<Arguments> provideValidValues() {
    return Stream.of(
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("123", new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(true, new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("1", new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(1, 2), new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of("123", "2"), new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(1, 2), new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(true, false), new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of("1", "2"), new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("MALE", new FieldImpl("SEX", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("FEMALE", new FieldImpl("SEX", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("UNKNOWN", new FieldImpl("SEX", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("AFFECTED",
            new FieldImpl("AFFECTED_STATUS", FieldType.SAMPLE, ValueType.STRING,
                ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("UNAFFECTED",
            new FieldImpl("AFFECTED_STATUS", FieldType.SAMPLE, ValueType.STRING,
                ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("MISSING", new FieldImpl("AFFECTED_STATUS", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("NO_CALL", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("HOM_REF", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("HET", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("HOM_VAR", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("UNAVAILABLE", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("MIXED", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(1, 2), new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(2).build(), null, null)),
        Arguments.of(List.of("123", "2"), new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(2).build(), null, null)),
        Arguments.of(List.of(1, 2), new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.VARIABLE).build(), null, null)),
        Arguments.of(List.of(true, false), new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.A).build(), null, null)),
        Arguments.of(List.of("1", "2"), new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.G).build(), null, null))
    );
  }

  private static Stream<Arguments> provideInvalidValues() {
    return Stream.of(
        Arguments.of("TEST", new FieldImpl("SEX", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("TEST", new FieldImpl("AFFECTED_STATUS", FieldType.SAMPLE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("TEST", new FieldImpl("TYPE", FieldType.GENOTYPE, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("1", new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("1", new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of("true", new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(1, "2"), new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of("123", 2), new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(1, "2"), new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of(true, "false"), new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of("1", 2), new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(List.of("1", "12"), new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.FIXED).count(1).build(), null, null)),
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.INTEGER,
            ValueCount.builder().type(Type.FIXED).count(2).build(), null, null)),
        Arguments.of("123", new FieldImpl("id", FieldType.INFO, ValueType.STRING,
            ValueCount.builder().type(Type.FIXED).count(2).build(), null, null)),
        Arguments.of(1, new FieldImpl("id", FieldType.INFO, ValueType.FLOAT,
            ValueCount.builder().type(Type.VARIABLE).build(), null, null)),
        Arguments.of(true, new FieldImpl("id", FieldType.INFO, ValueType.FLAG,
            ValueCount.builder().type(Type.A).build(), null, null)),
        Arguments.of("1", new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.G).build(), null, null)),
        Arguments.of("1", new FieldImpl("id", FieldType.INFO, ValueType.CHARACTER,
            ValueCount.builder().type(Type.R).build(), null, null))
    );
  }
}