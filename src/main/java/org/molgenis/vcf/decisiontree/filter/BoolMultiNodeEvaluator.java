package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.filter.BoolMultiNodeEvaluator.TripleBoolean.FALSE;
import static org.molgenis.vcf.decisiontree.filter.BoolMultiNodeEvaluator.TripleBoolean.MISSING;
import static org.molgenis.vcf.decisiontree.filter.BoolMultiNodeEvaluator.TripleBoolean.TRUE;
import static org.molgenis.vcf.decisiontree.filter.model.BoolMultiQuery.Operator.AND;

import org.molgenis.vcf.decisiontree.filter.model.BoolMultiQuery;
import org.molgenis.vcf.decisiontree.filter.model.BoolMultiNode;
import org.molgenis.vcf.decisiontree.filter.model.BoolQuery;
import org.molgenis.vcf.decisiontree.filter.model.Field;
import org.molgenis.vcf.decisiontree.filter.model.MissingField;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class BoolMultiNodeEvaluator implements BaseBoolNodeEvaluator<BoolMultiNode> {

  public enum TripleBoolean {TRUE, FALSE, MISSING}

  @Override
  public NodeOutcome evaluate(BoolMultiNode node,
      Variant variant, Integer sampleIndex) {
    if (containsMissingFields(node, variant)) {
      return node.getOutcomeMissing();
    }
    for (BoolMultiQuery clause : node.getClauses()) {
      if (evaluateClause(variant, clause, sampleIndex) == MISSING) {
        return node.getOutcomeMissing();
      } else if (evaluateClause(variant, clause, sampleIndex) == TRUE) {
        return clause.getOutcomeTrue();
      }
    }
    return node.getOutcomeDefault();
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

  private TripleBoolean evaluateClause(Variant variant,
      BoolMultiQuery clause, @Nullable Integer sampleIndex) {
    TripleBoolean outcome = FALSE;
    if (clause.getQueryList().size() == 1) {
      BoolQuery query = clause.getQueryList().get(0);
      Object value = variant.getValue(query.getField(), sampleIndex);
      if (isMissingValue(value)) {
        outcome = MISSING;
      } else if (executeQuery(query, value)) {
        outcome = TRUE;
      }
    } else {
      if (evaluateMultiQuery(clause, variant, sampleIndex)) {
        outcome = TRUE;
      }
    }
    return outcome;
  }

  private boolean evaluateMultiQuery(BoolMultiQuery clause, Variant variant, Integer sampleIndex) {
    if (clause.getOperator() == AND) {
      if (allQueriesMatch(clause, variant, sampleIndex)) {
        return true;
      }
    } else {
      for (BoolQuery query : clause.getQueryList()) {
        Object value = variant.getValue(query.getField(), sampleIndex);
        if (!isMissingValue(value) && executeQuery(query, value)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean allQueriesMatch(BoolMultiQuery clause, Variant variant, Integer sampleIndex) {
    for (BoolQuery query : clause.getQueryList()) {
      Object value = variant.getValue(query.getField(), sampleIndex);
      if (isMissingValue(value) || !executeQuery(query, value)) {
        return false;
      }
    }
    return true;
  }
}
