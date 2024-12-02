package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;
import static org.molgenis.vcf.decisiontree.filter.VcfMetadata.isPerAlleleValue;
import static org.molgenis.vcf.decisiontree.filter.VcfMetadata.isSingleValueField;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FIELD_PREFIX;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.GENOTYPE;
import static org.molgenis.vcf.decisiontree.filter.model.FieldType.SAMPLE;
import static org.molgenis.vcf.decisiontree.filter.model.SampleFieldType.AFFECTED_STATUS;
import static org.molgenis.vcf.decisiontree.filter.model.SampleFieldType.SEX;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.EQUALS;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.NOT_EQUALS;

import htsjdk.variant.variantcontext.GenotypeType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeValidationException;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.molgenis.vcf.utils.sample.model.AffectedStatus;
import org.molgenis.vcf.utils.sample.model.Sex;

public class ValueValidator {

  public static final String MESSAGE = "Query value '%s' is of type '%s' while INFO field is of type '%s' for node '%s'.";
  public static final String INVALID_ENUM_MESSAGE = "Value '%s' is not a valid value for '%s', valid values: %s.";

  private ValueValidator() {
  }

  public static void validate(ConfigDecisionTree configDecisionTree, VcfMetadata vcfMetadata) {
    Map<String, ConfigNode> nodes = configDecisionTree.getNodes();
    nodes.forEach((key, value) -> validateValue(key, value, vcfMetadata));
  }

  private static void validateValue(String nodeId, ConfigNode node, VcfMetadata vcfMetadata) {
    switch (node.getType()) {
      case BOOL:
        ConfigBoolNode boolNode = (ConfigBoolNode) node;
        validateQueryValue(nodeId, boolNode.getQuery(), vcfMetadata);
        break;
      case BOOL_MULTI:
        ConfigBoolMultiNode boolMultiNode = (ConfigBoolMultiNode) node;
        boolMultiNode.getOutcomes().forEach(
            configBoolMultiQuery -> configBoolMultiQuery.getQueries()
                .forEach(query -> validateQueryValue(nodeId, query, vcfMetadata)));
        break;
      case CATEGORICAL, EXISTS, LEAF:
        break;
      default:
        throw new UnexpectedEnumException(node.getType());
    }
  }

  private static void validateQueryValue(String nodeId, ConfigBoolQuery query,
      VcfMetadata vcfMetadata) {
    Field field = vcfMetadata.getField(query.getField());
    if (!(field instanceof MissingField)) {
      Object value = query.getValue();
      validateEnumFields(field, value);
      validateQueryValue(nodeId, field, query);
    }
  }

  private static void validateEnumFields(Field field, Object value) {
    String genotypeTypeField = "TYPE";
    String sexField = SEX.name();
    String affectedField = AFFECTED_STATUS.name();
    if (field.getId().equals(genotypeTypeField) && field.getFieldType() == GENOTYPE) {
      if (Arrays.stream(GenotypeType.values()).map(GenotypeType::name)
          .noneMatch(enumValue -> enumValue.equals(
              value))) {
        throw new ConfigDecisionTreeValidationException(
            format(INVALID_ENUM_MESSAGE, value,
                sexField, Arrays.stream(GenotypeType.values()).map(GenotypeType::name).toList()));
      }
    } else if (field.getFieldType() == SAMPLE) {
      validateSampleEnums(field, value, sexField, affectedField);
    }
  }

  private static void validateSampleEnums(Field field, Object value, String sexField,
      String affectedField) {
    if (field.getId().equals(sexField)) {
      if (Arrays.stream(Sex.values()).map(Sex::name)
          .noneMatch(enumValue -> enumValue.equals(value))) {
        throw new ConfigDecisionTreeValidationException(
            format(INVALID_ENUM_MESSAGE, value,
                sexField, Arrays.stream(Sex.values()).map(Sex::name).toList()));
      }
    } else if (field.getId().equals(affectedField) && Arrays.stream(AffectedStatus.values())
        .map(AffectedStatus::name)
        .noneMatch(enumValue -> enumValue.equals(
            value))) {
      throw new ConfigDecisionTreeValidationException(
          format(INVALID_ENUM_MESSAGE, value,
              affectedField,
              Arrays.stream(AffectedStatus.values()).map(AffectedStatus::name).toList()));
    }
  }

  private static void validateQueryValue(String nodeId, Field field, ConfigBoolQuery query) {
    Object value = query.getValue();
    ConfigOperator operator = query.getOperator();
    if (value instanceof Collection<?>) {
      ((Collection<?>) value).forEach(
          singleValue -> validateSingleValue(nodeId, singleValue, field.getValueType()));
    } else {
      if (!(isSingleValueField(field) || isPerAlleleValue(field))&& List.of(EQUALS, NOT_EQUALS).contains(operator)) {
        throw new ConfigDecisionTreeValidationException(
            format(
                "Field '%s' in node '%s' contains a collection of values, therefore the '%s' query value should also have a collection as value.",
                field.getId(), nodeId, operator));
      }
      validateSingleValue(nodeId, value, field.getValueType());
    }
  }

  private static void validateSingleValue(String nodeId, Object singleValue, ValueType valueType) {
    if (!singleValue.toString().startsWith(FILE_PREFIX) && !singleValue.toString()
        .startsWith(FIELD_PREFIX)) {
      validateValueTypes(nodeId, singleValue, valueType);
    }
  }

  private static void validateValueTypes(String nodeId, Object singleValue, ValueType valueType) {
    switch (valueType) {
      case INTEGER, FLOAT:
        if (!Number.class.isAssignableFrom(singleValue.getClass())) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  MESSAGE,
                  singleValue, singleValue.getClass().getSimpleName(), valueType, nodeId));
        }
        break;
      case FLAG:
        if (!Boolean.class.isAssignableFrom(singleValue.getClass())) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  MESSAGE,
                  singleValue, singleValue.getClass().getSimpleName(), valueType, nodeId));
        }
        break;
      case STRING, CATEGORICAL:
        if (!(String.class.isAssignableFrom(singleValue.getClass()))) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  MESSAGE,
                  singleValue, singleValue.getClass().getSimpleName(), valueType, nodeId));
        }
        break;
      case CHARACTER:
        if (!(String.class.isAssignableFrom(singleValue.getClass()))) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  MESSAGE,
                  singleValue, singleValue.getClass().getSimpleName(), valueType, nodeId));
        }
        if (singleValue.toString().length() > 1) {
          throw new ConfigDecisionTreeValidationException(
              format(
                  "Value '%s' is more than one character long while the fieldtype of the field is 'CHARACTER' for node '%s'.",
                  singleValue, nodeId));
        }
        break;
      default:
        throw new UnexpectedEnumException(valueType);
    }
  }

}
