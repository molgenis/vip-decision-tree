package org.molgenis.vcf.decisiontree.runner;

import static java.lang.String.format;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FIELD_PREFIX;
import static org.molgenis.vcf.decisiontree.filter.model.BoolNode.FILE_PREFIX;

import java.util.Collection;
import java.util.Map;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.VcfMetadata;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.ConfigDecisionTreeValidationException;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolMultiNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolNode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigDecisionTree;
import org.molgenis.vcf.decisiontree.loader.model.ConfigNode;

public class ValueValidator {

  public static final String MESSAGE = "Query value '%s' is of type '%s' while INFO field is of type '%s' for node '%s'.";

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
      ValueType valueType = field.getValueType();
      Object value = query.getValue();
      validateQueryValue(nodeId, value, valueType);
    }
  }

  private static void validateQueryValue(String nodeId, Object value, ValueType valueType) {
    if (value instanceof Collection<?>) {
      ((Collection<?>) value).forEach(
          singleValue -> validateSingleValue(nodeId, singleValue, valueType));
    } else {
      validateSingleValue(nodeId, value, valueType);
    }
  }

  private static void validateSingleValue(String nodeId, Object singleValue, ValueType valueType) {
    if (!singleValue.toString().startsWith(FILE_PREFIX) && !singleValue.toString()
        .startsWith(FIELD_PREFIX)) {
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
        case STRING:
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

}
