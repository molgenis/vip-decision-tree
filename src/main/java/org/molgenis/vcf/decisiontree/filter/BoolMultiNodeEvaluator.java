package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.model.BoolClause.Operator.AND;

import org.molgenis.vcf.decisiontree.filter.model.BoolClause;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.stereotype.Component;

@Component
public class BoolMultiNodeEvaluator extends AbstractBoolNodeEvaluator implements
    NodeEvaluator<BoolMultiNode> {

  @Override
  public NodeOutcome evaluate(BoolMultiNode node, Variant variant) {
    NodeOutcome outcome = node.getOutcomeDefault();
    outcome = checkFields(node, variant, outcome);

    for (BoolClause clause : node.getClauses()) {
      outcome = evaluateClause(node, variant, outcome, clause);
    }
    return outcome;
  }

  private NodeOutcome checkFields(BoolMultiNode node, Variant variant, NodeOutcome outcome) {
    for (Field field : node.getFields()) {
      if (field instanceof MissingField) {
        if (node.getOutcomeMissing() != null) {
          outcome = node.getOutcomeMissing();
        } else {
          throw new EvaluationException(node, variant, "missing 'missingOutcome'");
        }
      }
    }
    return outcome;
  }

  private NodeOutcome evaluateClause(BoolMultiNode node, Variant variant, NodeOutcome outcome,
      BoolClause clause) {
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

  private boolean evaluateMultiQuery(BoolClause clause, Variant variant) {
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

  private boolean allQueriesMatch(BoolClause clause, Variant variant) {
    for (BoolQuery query : clause.getQueryList()) {
      Object value = variant.getValue(query.getField());
      if (isMissingValue(value) || !executeQuery(query, value)) {
        return false;
      }
    }
    return true;
  }
}
