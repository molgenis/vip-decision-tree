package org.molgenis.vcf.decisiontree.runner;

import java.util.Collection;
import java.util.Set;

import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.DecisionType;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.ValueCount.Type;
import org.molgenis.vcf.decisiontree.filter.model.ValueType;
import org.molgenis.vcf.decisiontree.loader.model.ConfigBoolQuery;
import org.molgenis.vcf.decisiontree.loader.model.ConfigMultiMode;
import org.molgenis.vcf.decisiontree.loader.model.ConfigOperator;
import org.molgenis.vcf.utils.UnexpectedEnumException;
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
                case EQUALS, NOT_EQUALS, EQUALS_SEQUENCE, NOT_EQUALS_SEQUENCE:
                    validateEquals(field, configBoolQuery);
                    break;
                case RANGE_OVERLAPS, RANGE_ABOVE, RANGE_BELOW:
                    validateRange(field, configBoolQuery);
                    break;
                default:
                    throw new UnexpectedEnumException(configBoolQuery.getOperator());
            }
        }
    }

    private void validateRange(Field field, ConfigBoolQuery query) {
        validateFileValueAllowed(query, field);
        if (field.getValueType() != ValueType.RANGE ) {
            throw new UnsupportedValueTypeException(field, DecisionType.BOOL);
        }
        validateRangeAlleleCount(field, DecisionType.BOOL);
    }

    private void validateRangeAlleleCount(Field field, DecisionType bool) {
        if (field.getValueCount().getType() != Type.FIXED && (field.getValueCount().getType() == Type.FIXED
                && field.getValueCount().getCount() != 2)) {
            throw new UnsupportedOperationException("FIXME: unsupported count for range.");
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
        if(query.getMultiMode() == ConfigMultiMode.SINGLE) {
            validateSingleOrPerAlleleCount(field, DecisionType.BOOL, IN);
        }
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
        } else if (field.getValueCount().getType() == Type.FIXED
                && field.getValueCount().getCount() == 1) {
            throw new UnsupportedValueCountException(field, DecisionType.BOOL, CONTAINS);
        }
    }

    private void validateSingleOrPerAlleleCount(Field field, DecisionType decisionType, ConfigOperator configOperator) {
        if (field.getValueCount().getType() == Type.G ||
                field.getValueCount().getType() == Type.VARIABLE) {
            throw new UnsupportedValueCountTypeException(field, decisionType, configOperator);
        } else if (field.getValueCount().getType() == Type.FIXED
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
