package org.molgenis.vcf.decisiontree.filter;

import static org.molgenis.vcf.decisiontree.utils.VcfUtils.getValue;

import java.util.Collection;
import java.util.stream.Collectors;
import org.molgenis.vcf.decisiontree.filter.model.CategoricalNode;
import org.molgenis.vcf.decisiontree.filter.model.NodeOutcome;
import org.molgenis.vcf.decisiontree.utils.VcfUtils;

public class CategoricalNodeEvaluator implements NodeEvaluator<CategoricalNode> {
  @Override
  public NodeOutcome evaluate(CategoricalNode node, Variant variant) {
    Object value = getValue(node.getField(), variant);
    if (value instanceof Collection) {
      throw new UnsupportedOperationException(
          "Nodes of type 'enum' cannot be used with multivalue fields.");
    } else {
      if (value == null) {
        NodeOutcome outcomeMissing = node.getOutcomeMissing();
        if (outcomeMissing != null) {
          return outcomeMissing;
        } else {
          throw new RuntimeException(
              "missing value detected but no missing outcome defined in filter");
        }
      }
      NodeOutcome ne = node.getOutcomeMap().get(value);
      if (ne != null) {
        return ne;
      } else {
        NodeOutcome outcomeDefault = node.getOutcomeDefault();
        if (outcomeDefault != null) {
          return outcomeDefault;
        } else {
          throw new EvaluationException(
              VcfUtils.getVariantIdentifier(variant)
                  + " "
                  + node.getField().stream().collect(Collectors.joining("/"))
                  + ": unmapped value '"
                  + value
                  + "' detected in filter '"
                  + "TODO_ADD_ID_TO_NODE"
                  + "'. suggestion: 1) add an entry to 'outcomeMap' or add a 'outcomeDefault'");
        }
      }
    }
  }
}
