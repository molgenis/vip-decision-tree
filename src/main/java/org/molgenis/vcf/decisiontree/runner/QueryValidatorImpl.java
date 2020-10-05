package org.molgenis.vcf.decisiontree.runner;

import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.IN;
import static org.molgenis.vcf.decisiontree.loader.model.ConfigOperator.NOT_IN;

import java.util.Collection;
import java.util.Set;
import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.springframework.stereotype.Component;

@Component
public class QueryValidatorImpl implements QueryValidator {

  public static final Set<ConfigOperator> ALLOWED_FILE_OPERATORS = Set.of(IN, NOT_IN);

  @Override
  public void validateBooleanNode(ConfigBoolQuery configBoolQuery, Field field) {
    switch (configBoolQuery.getOperator()) {
      case GREATER:
      case LESS:
      case LESS_OR_EQUAL:
      case GREATER_OR_EQUAL:
        validateLesserGreater(field, configBoolQuery);
        break;
      case IN:
      case NOT_IN:
        validateIn(field);
        break;
      case CONTAINS:
      case NOT_CONTAINS:
        validateContains(field, configBoolQuery);
        break;
      case EQUALS:
      case NOT_EQUALS:
        validateEquals(field, configBoolQuery);
        break;
      default:
        throw new UnexpectedEnumException(configBoolQuery.getOperator());
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
    validateSingleOrPerAlleleCount(field, DecisionType.BOOL);
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
      validateSingleOrPerAlleleCount(field, DecisionType.BOOL);
    }
  }

  private void validateContains(Field field, ConfigBoolQuery configBoolQuery) {
    validateFileValueAllowed(configBoolQuery, field);
    if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
      throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
    } else if (field.getValueCount().getType() == Type.FIXED
        && field.getValueCount().getCount() == 1) {
      throw new UnsupportedValueCountException(field, DecisionType.BOOL);
    }
  }

  private void validateSingleOrPerAlleleCount(Field field, DecisionType decisionType) {
    if (field.getValueCount().getType() == Type.G ||
        field.getValueCount().getType() == Type.VARIABLE) {
      throw new UnsupportedValueCountTypeException(field, decisionType);
    } else if (field.getValueCount().getType() == Type.FIXED
        && field.getValueCount().getCount() != 1){
      throw new UnsupportedValueCountException(field, decisionType);
    }
  }

  @Override
  public void validateCategoricalNode(Field field) {
    if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
      throw new UnsupportedValueTypeException(field, DecisionType.CATEGORICAL);
    }
    validateSingleOrPerAlleleCount(field, DecisionType.CATEGORICAL);
  }
}
