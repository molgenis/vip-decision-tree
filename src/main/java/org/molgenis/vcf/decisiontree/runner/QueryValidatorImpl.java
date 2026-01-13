package org.molgenis.vcf.decisiontree.runner;

import java.util.Collection;
import java.util.Set;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.molgenis.vcf.utils.metadata.ValueCount;
import org.molgenis.vcf.utils.metadata.ValueType;
import org.springframework.stereotype.Component;

import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.*;

@Component
public class QueryValidatorImpl implements QueryValidator {

  public static final Set<ConfigOperator> ALLOWED_FILE_OPERATORS = Set.of(IN, NOT_IN, CONTAINS, NOT_CONTAINS, CONTAINS_ALL, CONTAINS_ANY, CONTAINS_NONE);

  @Override
  public void validateBooleanNode(ConfigBoolQuery configBoolQuery, Field field) {
    if (!(field instanceof MissingField)) {
      switch (configBoolQuery.getOperator()) {
        case GREATER, LESS, LESS_OR_EQUAL, GREATER_OR_EQUAL:
          validateLesserGreater(field, configBoolQuery);
          break;
        case IN, NOT_IN:
          validateIn(field);
          break;
        case CONTAINS, NOT_CONTAINS, CONTAINS_ALL, CONTAINS_ANY, CONTAINS_NONE:
          validateContains(field, configBoolQuery);
          break;
        case EQUALS, NOT_EQUALS:
          validateEquals(field, configBoolQuery);
          break;
        case ANY_GREATER:
          //FIXME
          break;
        default:
          throw new UnexpectedEnumException(configBoolQuery.getOperator());
      }
    }
  }

  private void validateFileValueAllowed(ConfigBoolQuery query, Field field) {
    ConfigOperator operator = query.getOperator();
    if (!ALLOWED_FILE_OPERATORS.contains(operator) && query.getValue().toString().startsWith(
        BoolNode.FILE_PREFIX)) {
      throw new FileValueNotAllowedException(operator, ALLOWED_FILE_OPERATORS.toString(),
          field.getId());
    }
  }

  private void validateLesserGreater(Field field, ConfigBoolQuery query) {
    validateFileValueAllowed(query, field);
    if (field.getValueType() != ValueType.FLOAT && field.getValueType() != ValueType.INTEGER) {
      throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
    }
    validateSingleOrPerAlleleCount(field, DecisionType.BOOL, IN);
  }

  private void validateEquals(Field field, ConfigBoolQuery configBoolQuery) {
    validateFileValueAllowed(configBoolQuery, field);

    if (field.getSeparator() != null && !(configBoolQuery.getValue() instanceof Collection)) {
      throw new CountMismatchException(configBoolQuery);
    }
  }

  private void validateIn(Field field) {
    if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
      throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
    } else {
      validateSingleOrPerAlleleCount(field, DecisionType.BOOL, IN);
    }
  }

  private void validateContains(Field field, ConfigBoolQuery configBoolQuery) {
    validateFileValueAllowed(configBoolQuery, field);
    if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
      throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
    } else if (field.getValueCount().getType() == ValueCount.Type.FIXED
        && field.getValueCount().getCount() == 1) {
      throw new UnsupportedValueCountException(field, DecisionType.BOOL, CONTAINS);
    }
  }

  private void validateSingleOrPerAlleleCount(Field field, DecisionType decisionType, ConfigOperator configOperator) {
    if (field.getValueCount().getType() == ValueCount.Type.G ||
        field.getValueCount().getType() == ValueCount.Type.VARIABLE) {
      throw new UnsupportedValueCountTypeException(field, decisionType, configOperator);
    } else if (field.getValueCount().getType() == ValueCount.Type.FIXED
        && field.getValueCount().getCount() != 1) {
      throw new UnsupportedValueCountException(field, decisionType, configOperator);
    }
  }

  @Override
  public void validateCategoricalNode(Field field) {
    if (!(field instanceof MissingField)) {
      if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
        throw new UnsupportedValueTypeException(field, DecisionType.CATEGORICAL);
      }
      validateSingleOrPerAlleleCount(field, DecisionType.CATEGORICAL, IN);
    }
  }
}
