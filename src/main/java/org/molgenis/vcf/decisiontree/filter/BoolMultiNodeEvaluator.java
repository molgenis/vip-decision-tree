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

    for (Field field : node.getFields()) {
      if (field instanceof MissingField) {
        if (node.getOutcomeMissing() != null) {
          return node.getOutcomeMissing();
        } else {
          throw new EvaluationException(node, variant, "missing 'missingOutcome'");
        }
      }
    }

    for (BoolClause clause : node.getClauses()) {
      if (clause.getQueryList().size() > 1 && clause.getOperator() == null) {
        throw new EvaluationException(node, variant,
            "Clause has more than one query without an AND/OR operator.");
      } else if (clause.getQueryList().size() == 1) {
        BoolQuery query = clause.getQueryList().get(0);
        Object value = variant.getValue(query.getField());
        if (isMissingValue(value)) {
          return node.getOutcomeMissing();
        }
        if (executeQuery(query, value)) {
          return clause.getOutcomeTrue();
        }
      } else {
        if (evaluateMultiQuery(clause, variant)) {
          return clause.getOutcomeTrue();
        }
      }
    }
    return node.getOutcomeDefault();
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
