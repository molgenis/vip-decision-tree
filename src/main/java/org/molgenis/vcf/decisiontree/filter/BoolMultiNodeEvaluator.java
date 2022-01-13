package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolMultiQuery.Operator.AND;

import org.molgenis.vcf.decisiontree.filter.model.BoolMultiQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class BoolMultiNodeEvaluator implements BaseBoolNodeEvaluator<BoolMultiNode> {

  @Override
  public NodeOutcome evaluate(BoolMultiNode node, Variant variant) {
    NodeOutcome outcome = node.getOutcomeDefault();

    if (containsMissingFields(node, variant)) {
      return node.getOutcomeMissing();
    }

    for (BoolMultiQuery clause : node.getClauses()) {
      outcome = evaluateClause(node, variant, outcome, clause);
    }
    return outcome;
  }

  private boolean containsMissingFields(BoolMultiNode node, Variant variant) {
    for (Field field : node.getFields()) {
      if (field instanceof MissingField) {
        if (node.getOutcomeMissing() != null) {
          return true;
        } else {
          throw new EvaluationException(node, variant, "missing 'missingOutcome'");
        }
      }
    }
    return false;
  }

  private NodeOutcome evaluateClause(BoolMultiNode node, Variant variant, NodeOutcome outcome,
      BoolMultiQuery clause) {
    if (clause.getQueryList().size() == 1) {
      BoolQuery query = clause.getQueryList().get(0);
      Object value = variant.getValue(query.getField());
      if (isMissingValue(value)) {
        outcome = node.getOutcomeMissing();
      } else if (executeQuery(query, value)) {
        outcome = clause.getOutcomeTrue();
      }
    } else {
      if (evaluateMultiQuery(clause, variant)) {
        outcome = clause.getOutcomeTrue();
      }
    }
    return outcome;
  }

  private boolean evaluateMultiQuery(BoolMultiQuery clause, Variant variant) {
    if (clause.getOperator() == AND) {
      if (allQueriesMatch(clause, variant)) {
        return true;
      }
    } else {
      for (BoolQuery query : clause.getQueryList()) {
        Object value = variant.getValue(query.getField());
        if (!isMissingValue(value) && executeQuery(query, value)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean allQueriesMatch(BoolMultiQuery clause, Variant variant) {
    for (BoolQuery query : clause.getQueryList()) {
      Object value = variant.getValue(query.getField());
      if (isMissingValue(value) || !executeQuery(query, value)) {
        return false;
      }
    }
    return true;
  }
}
