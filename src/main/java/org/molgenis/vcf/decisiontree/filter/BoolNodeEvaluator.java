package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator.IN;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getValue;
import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getVariantIdentifier;

import java.util.Collection;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.model.BoolNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery.Operator;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;
import org.springframework.stereotype.Component;

@Component
public class BoolNodeEvaluator implements NodeEvaluator<BoolNode> {

  @Override
  public NodeOutcome evaluate(BoolNode node, Variant variant) {
    Object value = getValue(node.getQuery().getField(), variant);
    if (value == null) {
      NodeOutcome outcomeMissing = node.getOutcomeMissing();
      if (outcomeMissing != null) {
        return outcomeMissing;
      } else {
        throw new EvaluationException(
            VcfUtils.getVariantIdentifier(variant)
                + " "
                + node.getQuery().getField().stream().collect(Collectors.joining("/"))
                + ": null value detected in filter '"
                + "TODO_ADD_ID_TO_NODE"
                + "'. suggestion: add an 'outcomeMissing' property to the filter definition.");
      }
    }
    boolean matches = executeQuery(node, variant);
    return matches ? node.getOutcomeTrue() : node.getOutcomeFalse();
  }

  private boolean executeQuery(BoolNode node, Variant rfc) {
    BoolQuery query = node.getQuery();
    Object value = getValue(query.getField(), rfc);
    Operator operator = query.getOperator();
    try {
      // FIXME: better validation and conversion of numeric or collection values
      Double doubleValue;
      switch (operator) {
        case EQUALS:
          return value.equals(query.getValue());
        case NOT_EQUALS:
          return !value.equals(query.getValue());
        case GREATER:
          doubleValue = Double.parseDouble(value.toString());
          return doubleValue > Double.parseDouble(query.getValue().toString());
        case GREATER_OR_EQUAL:
          doubleValue = Double.parseDouble(value.toString());
          return doubleValue >= Double.parseDouble(query.getValue().toString());
        case LESS:
          doubleValue = Double.parseDouble(value.toString());
          return doubleValue < Double.parseDouble(query.getValue().toString());
        case LESS_OR_EQUAL:
          doubleValue = Double.parseDouble(value.toString());
          return doubleValue <= Double.parseDouble(query.getValue().toString());
        case IN:
        case NOT_IN:
          Collection<Object> filterValue = (Collection<Object>) query.getValue();
          return filterValue.contains(value) == (operator == IN);
        default:
          throw new IllegalStateException(
              String.format("Unknown operator: %s", query.getOperator()));
      }
    } catch (NumberFormatException e) {
      // FIXME: proper exception
      throw new RuntimeException(
          String.format(
              "Value '%s' for field '%s' in variant '%s' is not a number",
              value, node.getQuery().getField(), getVariantIdentifier(rfc)));
    }
  }
}
