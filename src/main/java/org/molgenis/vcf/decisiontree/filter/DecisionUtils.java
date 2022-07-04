package org.molgenis.vcf.decisiontree.filter;

import static java.util.stream.Collectors.joining;

import htsjdk.variant.vcf.VCFConstants;
import java.util.List;
import java.util.Set;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

public class DecisionUtils {

  public static String getDecisionLabelsString(Decision decision) {
    String decisionLabel = "";
    Set<Label> labels = decision.getLabels();
    if (!labels.isEmpty()) {
      decisionLabel = labels.stream().map(Label::getId).sorted().collect(joining("&"));
    }
    return decisionLabel;
  }

  public static String getDecisionsPath(Decision decision) {
    String decisionPath;
    List<Node> path = decision.getPath();
    if (!path.isEmpty()) {
      decisionPath = path.stream().map(Node::getId).collect(joining("&"));
    } else {
      decisionPath = VCFConstants.MISSING_VALUE_v4;
    }
    return decisionPath;
  }

  public static String getDecisionClass(Decision decision) {
    String infoClassValue;
    if (decision == null) {
      infoClassValue = "";
    } else {
      infoClassValue = decision.getClazz();
    }
    return infoClassValue;
  }
}
