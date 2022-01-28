package org.molgenis.vcf.decisiontree.filter;

import static java.util.stream.Collectors.joining;

import htsjdk.variant.vcf.VCFConstants;
import java.util.List;
import java.util.Set;
import org.molgenis.vcf.decisiontree.filter.model.Decision;
import org.molgenis.vcf.decisiontree.filter.model.Label;
import org.molgenis.vcf.decisiontree.filter.model.Node;

public class ConsequenceAnnotatorImpl implements ConsequenceAnnotator {

  private final boolean writeLabels;
  private final boolean writePaths;

  public static final String INFO_CLASS_ID = "VIPC";
  public static final String INFO_PATH_ID = "VIPP";
  public static final String INFO_LABELS_ID = "VIPL";


  public ConsequenceAnnotatorImpl(boolean writeLabels, boolean writePaths) {
    this.writeLabels = writeLabels;
    this.writePaths = writePaths;
  }

  @Override
  public String annotate(Decision decision, String consequence) {
    StringBuilder csqBuilder = new StringBuilder(consequence);
    csqBuilder.append("|");
    csqBuilder.append(getDecisionClass(decision));
    if (writePaths) {
      csqBuilder.append("|");
      csqBuilder.append(getDecisionsPath(decision));
    }
    if (writeLabels) {
      csqBuilder.append("|");
      csqBuilder.append(getDecisionLabelsString(decision));
    }
    return csqBuilder.toString();
  }

  private static String getDecisionLabelsString(Decision decision) {
    String decisionLabel = "";
    Set<Label> labels = decision.getLabels();
    if (!labels.isEmpty()) {
      decisionLabel = labels.stream().map(Label::getId).collect(joining("&"));
    }
    return decisionLabel;
  }

  private static String getDecisionsPath(Decision decision) {
    String decisionPath;
    List<Node> path = decision.getPath();
    if (!path.isEmpty()) {
      decisionPath = path.stream().map(Node::getId).collect(joining("&"));
    } else {
      decisionPath = VCFConstants.MISSING_VALUE_v4;
    }
    return decisionPath;
  }

  private static String getDecisionClass(Decision decision) {
    String infoClassValue;
    if (decision == null) {
      infoClassValue = "";
    } else {
      infoClassValue = decision.getClazz();
    }
    return infoClassValue;
  }
}
