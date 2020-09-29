package org.molgenis.vcf.decisiontree.runner;

import org.molgenis.vcf.decisiontree.UnexpectedEnumException;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.springframework.stereotype.Component;

@Component
public class QueryValidatorImpl implements QueryValidator {

  @Override
  public void validateBooleanNode(ConfigBoolQuery configBoolQuery, Field field) {
    switch (configBoolQuery.getOperator()) {
      case GREATER:
      case LESS:
      case LESS_OR_EQUAL:
      case GREATER_OR_EQUAL:
        if (field.getValueType() != ValueType.FLOAT && field.getValueType() != ValueType.INTEGER) {
          throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
        }
        validateSingleOrPerAlleleCount(field, DecisionType.BOOL);
        break;
      case IN:
      case NOT_IN:
        if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
          throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
        } else {
          validateSingleOrPerAlleleCount(field, DecisionType.BOOL);
        }
        break;
      case CONTAINS:
      case NOT_CONTAINS:
        if (field.getValueType() == ValueType.FLAG || field.getValueType() == ValueType.FLOAT) {
          throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
        } else if (field.getValueCount().getType() == Type.FIXED
            && field.getValueCount().getCount() == 1) {
          throw new UnsupportedValueCountException(field, DecisionType.BOOL);
        }
        break;
      case EQUALS:
      case NOT_EQUALS:
        if(field.getSeparator() != null){
          throw new UnsupportedMultiValueOperatorException(field, configBoolQuery.getOperator());
        }
        break;
      default:
        throw new UnexpectedEnumException(configBoolQuery.getOperator());
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
