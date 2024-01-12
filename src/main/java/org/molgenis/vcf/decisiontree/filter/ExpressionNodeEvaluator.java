package org.molgenis.vcf.decisiontree.filter;

import org.molgenis.vcf.decisiontree.filter.model.*;
import org.molgenis.vcf.utils.UnexpectedEnumException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExpressionNodeEvaluator{
    public NodeOutcome evaluate(
            ExpressionNode node, Variant variant, @Nullable SampleContext sampleContext) {
        NodeOutcome nodeOutcome;
        String expression = node.getExpression().getCalculation();
        for (Map.Entry<String, Field> entry : node.getExpression().getFields().entrySet()) {
            Field field = entry.getValue();
            if (field instanceof MissingField) {
                if (node.getOutcomeMissing() != null) {
                    return node.getOutcomeMissing();
                } else {
                    throw new EvaluationException(node, variant, "missing 'missingOutcome'");
                }
            }
            Object value = variant.getValue(field, sampleContext);
            if (!isMissingValue(value)) {
                expression = expression.replace("$"+entry.getKey(), value.toString());
            } else {
                nodeOutcome = node.getOutcomeMissing();
                if (nodeOutcome == null) {
                    throw new EvaluationException(node, variant, "missing 'missingOutcome'");
                }
                return nodeOutcome;
            }
        }

        Double result;
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        try {
            result = (Double) scriptEngine.eval(expression);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        boolean matches = executeQuery(result, node.getExpression().getOperator(), node.getExpression().getValue());
        nodeOutcome = matches ? node.getOutcomeTrue() : node.getOutcomeFalse();
        return nodeOutcome;
    }
    private boolean isMissingValue(Object value) {
        return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
    }

    private boolean executeQuery(Double calculatedValue, Expression.Operator operator, Object value) {
        boolean matches;

        switch (operator) {
            case EQUALS:
                matches = value.equals(calculatedValue);
                break;
            case NOT_EQUALS:
                matches = !value.equals(calculatedValue);
                break;
            case LESS:
                matches = executeLessQuery(value, calculatedValue);
                break;
            case LESS_OR_EQUAL:
                matches = !executeGreaterQuery(value, calculatedValue);
                break;
            case GREATER:
                matches = executeGreaterQuery(value, calculatedValue);
                break;
            case GREATER_OR_EQUAL:
                matches = !executeLessQuery(value, calculatedValue);
                break;
            default:
                throw new UnexpectedEnumException(operator);
        }

        return matches;
    }

    @SuppressWarnings("DuplicatedCode")
    private boolean executeLessQuery(Object value, Object queryValue) {
        return ((Double) value) < ((Double) queryValue);
    }

    @SuppressWarnings("DuplicatedCode")
    private boolean executeGreaterQuery(Object value, Object queryValue) {
        return ((Double) value) > ((Double) queryValue);
    }

}
